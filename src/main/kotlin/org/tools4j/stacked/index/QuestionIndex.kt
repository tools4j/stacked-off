package org.tools4j.stacked.index

import mu.KLogging
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.en.EnglishAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.*
import org.apache.lucene.queries.CustomScoreProvider
import org.apache.lucene.queries.CustomScoreQuery
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.*
import org.apache.lucene.search.highlight.*
import org.apache.lucene.search.join.CheckJoinIndex
import org.apache.lucene.search.join.QueryBitSetProducer
import org.apache.lucene.search.join.ScoreMode
import org.apache.lucene.search.join.ToParentBlockJoinQuery
import java.io.IOException
import java.lang.Integer.min
import java.util.*


class QuestionIndex(indexFactory: IndexFactory, var indexedSiteIndex: IndexedSiteIndex): Initializable, Shutdownable {
    val docIndex = DocIndex(indexFactory, "questions");
    private val parentsFilter = QueryBitSetProducer(TermQuery(Term("child", "N")))
    private lateinit var analyzer: Analyzer
    lateinit var queryParser: QueryParser
    companion object: KLogging()

    override fun init() {
        val fields: MutableMap<String, Float> = getIndexedFieldsAndRankings()
        analyzer = EnglishAnalyzer()
        queryParser = MultiFieldQueryParser(
            fields.keys.toTypedArray(),
            analyzer,
            fields
        )
        docIndex.init()
    }

    override fun shutdown() {
        docIndex.shutdown()
    }

    private fun getIndexedFieldsAndRankings(): MutableMap<String, Float> = mutableMapOf(
        "title" to 10.0f, //posts
        "textContent" to 7.0f)  //posts & comments

    fun addDocsAsBlock(docs: List<Document>){
        docIndex.addDocsAsBlock(docs);
    }

    fun purgeSite(indexedSiteId: String) {
        docIndex.purgeSite(indexedSiteId)
    }

    fun purgeSites(indexedSiteIds: List<String>) {
        for(indexedSiteId in indexedSiteIds){
            purgeSite(indexedSiteId)
        }
    }

    fun getAll(): List<Question>{
        return search(MatchAllDocsQuery(), GetMaxSizeCollector());
    }

    fun search(searchTerm: String, fromDocIndexInclusive: Int = 0, toDocIndexExclusive: Int = 10, provideExplainPlans: Boolean = false): List<Question>{
        return search(parseSearchTerms(searchTerm), RangeCollector(fromDocIndexInclusive, toDocIndexExclusive))
    }

    fun search(q: Query, docCollector: DocCollector, provideExplainPlans: Boolean = false): List<Question> {
        return searchQuestionDocs(q, docCollector).map { getDocsForQuestion(it).convertToQuestion() }
    }

    fun searchForQuestionSummaries(searchTerm: String, fromDocIndexInclusive: Int = 0, toDocIndexExclusive: Int = 10, provideExplainPlans: Boolean = false): SearchResults {
        return searchForQuestionSummaries(parseSearchTerms(searchTerm), RangeCollector(fromDocIndexInclusive, toDocIndexExclusive, provideExplainPlans))
    }

    private fun parseSearchTerms(searchTerm: String): Query {
        return queryParser.parse(searchTerm)
    }

    fun searchForQuestionSummaries(q: Query, docCollector: DocCollector): SearchResults {
        val fragmenter = Fragmenter(docIndex, q, analyzer)
        val startMs = System.currentTimeMillis()
        val docs = searchQuestionDocs(q, docCollector)
        val questionSummaries = docs.map {
            val questionDocs = getDocsForQuestion(it)
            fragmenter.getQuestionSummary(questionDocs)
        }
        return SearchResults(
            questionSummaries,
            if(docs.maxScore.isNaN()) 0.0f else docs.maxScore,
            docs.totalHits,
            System.currentTimeMillis() - startMs)
    }

    private fun searchQuestionDocs(q: Query, docCollector: DocCollector, provideExplainPlans: Boolean = false): Docs {
        val childQueryBuilder = BooleanQuery.Builder()
        childQueryBuilder.add(BooleanClause(q, BooleanClause.Occur.MUST))
        childQueryBuilder.add(BooleanClause(TermQuery(Term("child", "Y")), BooleanClause.Occur.MUST))
        val childQuery = ToParentBlockJoinQuery(childQueryBuilder.build(), parentsFilter, ScoreMode.Avg)

        val parentQueryBuilder = BooleanQuery.Builder()
        parentQueryBuilder.add(BooleanClause(q, BooleanClause.Occur.MUST))
        parentQueryBuilder.add(BooleanClause(TermQuery(Term("child", "N")), BooleanClause.Occur.MUST))
        val parentQuery = parentQueryBuilder.build()

//        val childAndParentQuery = AnswerCountBoostQuery(
//            DisjunctionMaxQuery(mutableListOf(childQuery, parentQuery), 0.5f),
//            1.0f)

        val childAndParentQuery = DisjunctionMaxQuery(mutableListOf(childQuery, parentQuery), 0.5f)

        val reader = DirectoryReader.open(docIndex.index)
        CheckJoinIndex.check(reader, parentsFilter)

        return docIndex.docIdIndex.searchByQueryForDocs(childAndParentQuery, docCollector)
    }

    private fun getDocsForQuestion(doc: Doc): DocsForQuestion {
        val questionDocWithId = DocWithId(doc.docId, docIndex.getSearcher().doc(doc.docId))
        val childDocIds = getChildDocIdsUsingBitset(docIndex.getSearcher(), doc.docId)
        val childDocAndIds = childDocIds.map { DocWithId(it, docIndex.getSearcher().doc(it)) }
        val indexedSite = indexedSiteIndex.getById(questionDocWithId.doc.get("indexedSiteId")!!)!!
        return DocsForQuestion(questionDocWithId, childDocAndIds, indexedSite, doc.score, doc.explanation)
    }

    fun getQuestionByUid(uid: String): Question? {
        val doc = docIndex.docIdIndex.getDocByTerms(mapOf("uid" to uid, "child" to "N"))
        if(doc == null) return null
        return getDocsForQuestion(doc).convertToQuestion();
    }

    private fun getChildDocIdsUsingBitset(
        indexSearcher: IndexSearcher,
        parentDocId: Int
    ): List<Int> {
        val start = System.currentTimeMillis()
        val indexReader = indexSearcher.indexReader
        val leaves = indexReader.leaves()
        val subIndex = ReaderUtil.subIndex(parentDocId, leaves)
        val leaf = leaves[subIndex]
        val localParentDocId = parentDocId - leaf.docBase;
        val childDocs = ArrayList<Int>()
        if (localParentDocId == 0) { // test for questionDoc==0 here to avoid passing -1 to prevSetBit later on
            // not a parent, or parent has no children
            return childDocs
        }
        val prevParent = parentsFilter.getBitSet(leaf).prevSetBit(localParentDocId - 1)
        for (childDoc in prevParent + 1 until localParentDocId) {
            childDocs.add(leaf.docBase + childDoc)
        }
        return childDocs
    }

    fun onNewDataAddedToIndex() {
        docIndex.onNewDataAddedToIndex()
    }

    fun commit() {
        docIndex.commit()
    }

    fun size(): Int {
        return docIndex.size()
    }
}

data class DocWithId(val docId: Int, val doc: Document)

open class DocsForQuestion(
    val questionDoc: DocWithId,
    val childDocs: List<DocWithId>,
    val indexedSite: IndexedSite,
    val queryScore: Float = 0.0f,
    val explanation: Explanation?
){
    fun convertToQuestion(): Question{
        val comments = childDocs.map{ it.doc }.filter { it.get("type") == "comment" }.map { Comment(it) }.toList()
        val answers = childDocs.map{ it.doc }.filter { it.get("type") == "answer" }.map { doc -> Answer(doc, comments.filter { it.postUid == doc.get("uid") }) }.toList()
        val question = Question(
            questionDoc.doc,
            indexedSite,
            comments.filter{it.postUid == questionDoc.doc.get("uid")},
            answers.filter{it.parentUid == questionDoc.doc.get("uid")},
            explanation
        )
        return question
    }

    val allDocs: List<DocWithId> by lazy {
        val allDocs = ArrayList<DocWithId>()
        allDocs.add(questionDoc)
        allDocs.addAll(childDocs)
        allDocs
    }
}

private class AnswerCountBoostQuery(subQuery: Query, val multiplier: Float) : CustomScoreQuery(subQuery) {
    // The CustomScoreProvider is what actually alters the score
    private inner class MyScoreProvider(context: LeafReaderContext) : CustomScoreProvider(context) {
        private val reader: LeafReader = context.reader()
        private val fieldsToLoad: MutableSet<String>

        init {

            // We create a HashSet which contains the name of the field
            // which we need. This allows us to retrieve the document
            // with only this field loaded, which is a lot faster.
            fieldsToLoad = HashSet()
            fieldsToLoad.add("answerCount")
        }

        @Throws(IOException::class)
        override fun customScore(doc_id: Int, currentScore: Float, valSrcScore: Float): Float {
            // Get the result document from the index
            val doc = reader.document(doc_id, fieldsToLoad)
            val field = doc.getField("answerCount")
            val number = field.numericValue().toInt()

            // This is just an example on how to alter the current score
            //val boost = number.toFloat() * multiplier

            val boost = if(number > 0) 100 else 0

            // Return the new score for this result, based on the
            // original lucene score.
            return currentScore + boost
        }
    }

    // Make sure that our CustomScoreProvider is being used.
    public override fun getCustomScoreProvider(context: LeafReaderContext): CustomScoreProvider {
        return MyScoreProvider(context)
    }
}

class QuestionSummary (
    val siteDomain: String?,
    val uid: String,
    val title: String?,
    val createdDate: String?,
    val numberOfAnswers: Int,
    val tags: String?,
    val score: String?,
    val searchResultText: String,
    val queryScore: Float,
    val queryExplanation: String?)

class SearchResults (
    val questionSummaries: List<QuestionSummary>,
    val maxScore: Float,
    val totalHits: Long,
    val queryTimeMs: Long)

class Fragmenter(
    val docIndex: DocIndex,
    val query: Query,
    val analyzer: Analyzer
) {
    val formatter = SimpleHTMLFormatter()
    val scorer = QueryScorer(query)
    val highlighter = Highlighter(formatter, scorer)
    val fragmenter = SimpleSpanFragmenter(scorer,100)

    init {
        highlighter.setTextFragmenter(fragmenter);
    }

    fun getQuestionSummary(docsForQuestion: DocsForQuestion): QuestionSummary {
        val questionDoc = docsForQuestion.questionDoc.doc
        val frags = docsForQuestion.allDocs
            .flatMap { getTextFragmentsForField(it, "textContent") }
            .sortedByDescending { it.score }
            .take(2)

        var summary = ""
        for(frag in frags){
            summary += frag
            if(summary.length > 120) break
            summary += " ..."
        }
        if(summary.isEmpty()){
            val questionTextContent = questionDoc.get("textContent")
            summary = questionTextContent.substring(0, min(questionTextContent.length, 120)) + "..."
        }

        summary = summary.replace(Regex("^[^\\w]]+"), "").trim()
        if(summary[0].isLowerCase()){
            summary = "..." + summary
        }

        val question = docsForQuestion.convertToQuestion()
        return QuestionSummary(
            question.indexedSite.seSite.urlDomain,
            question.uid,
            question.title,
            question.creationDate,
            question.answers.size,
            question.tags,
            question.score,
            summary,
            docsForQuestion.queryScore,
            docsForQuestion.explanation
                .toString()
                .replace(Regex("\\n\\), product of"), "product of")
                .trim()
                .replace("  ", "| ")
        )
    }

    private fun getTextFragmentsForField(doc: DocWithId, fieldName: String): List<TextFragment> {
        val stream = TokenSources.getAnyTokenStream(docIndex.getSearcher().indexReader, doc.docId, fieldName, analyzer)
        val frags = highlighter.getBestTextFragments(stream, doc.doc.get(fieldName), true, 2)
        return frags.toList()
    }
}

