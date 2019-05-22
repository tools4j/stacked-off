package org.tools4j.stacked.index

import mu.KLogging
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.ReaderUtil
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.*
import org.apache.lucene.search.highlight.*
import org.apache.lucene.search.join.*
import java.util.*


class QuestionIndex(indexFactory: IndexFactory, var indexedSiteIndex: IndexedSiteIndex): Initializable, Shutdownable {
    val docIndex = DocIndex(indexFactory, "questions");
    private val parentsFilter = QueryBitSetProducer(TermQuery(Term("child", "N")))
    private lateinit var analyzer: Analyzer
    lateinit var queryParser: QueryParser
    companion object: KLogging()

    override fun init() {
        val fields: MutableMap<String, Float> = getIndexedFieldsAndRankings()
        analyzer = StandardAnalyzer()
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
        "title" to 10.0f, //post
        "textContent" to 7.0f)  //comment

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

    fun search(searchTerm: String, pageSize: Int = 10, pageIndex: Int = 0): List<Question>{
        return search(queryParser.parse(searchTerm), PageCollector(pageSize, pageIndex))
    }

    fun search(q: Query, docCollector: DocCollector): List<Question> {
        return searchQuestionDocs(q, docCollector).scoreDocs.map { getQuestionDocs(it.doc, it.score).convertToQuestion() }
    }

    fun searchForQuestionSummaries(searchTerm: String, pageSize: Int = 10, pageIndex: Int = 0): SearchResults {
        return searchForQuestionSummaries(queryParser.parse(searchTerm), PageCollector(pageSize, pageIndex))
    }

    fun searchForQuestionSummaries(q: Query, docCollector: DocCollector): SearchResults {
        val fragmenter = Fragmenter(docIndex, q, analyzer)
        val topDocs = searchQuestionDocs(q, docCollector)
        val questionSummaries = topDocs.scoreDocs.map {
            val questionDocs = getQuestionDocs(it.doc, it.score)
            fragmenter.getQuestionSummary(questionDocs)
        }
        return SearchResults(
            questionSummaries,
            if(topDocs.maxScore.isNaN()) 0.0f else topDocs.maxScore,
            topDocs.totalHits)
    }

    private fun searchQuestionDocs(q: Query, docCollector: DocCollector): TopDocs {
        val childSearchQuery = BooleanQuery.Builder()
        childSearchQuery.add(BooleanClause(q, BooleanClause.Occur.MUST))
        childSearchQuery.add(BooleanClause(TermQuery(Term("child", "Y")), BooleanClause.Occur.MUST))
        val childQuery = ToParentBlockJoinQuery(childSearchQuery.build(), parentsFilter, ScoreMode.Avg)

        val parentQuery = BooleanQuery.Builder()
        parentQuery.add(BooleanClause(q, BooleanClause.Occur.MUST))
        parentQuery.add(BooleanClause(TermQuery(Term("child", "N")), BooleanClause.Occur.MUST))

        val childAndParentQueryBuilder = BooleanQuery.Builder()
        childAndParentQueryBuilder.add(BooleanClause(childQuery, BooleanClause.Occur.SHOULD))
        childAndParentQueryBuilder.add(BooleanClause(parentQuery.build(), BooleanClause.Occur.SHOULD))
        val childAndParentQuery = childAndParentQueryBuilder.build()

        val reader = DirectoryReader.open(docIndex.index)
        CheckJoinIndex.check(reader, parentsFilter)

        return docIndex.docIdIndex.searchByQueryForTopDocs(childAndParentQuery, docCollector)
    }

    private fun getQuestionDocs(questionDocId: Int, queryScore: Float = 0.0f): QuestionDocs {
        val questionDocWithId = DocWithId(questionDocId, docIndex.getSearcher().doc(questionDocId))
        val childDocIds = getChildDocIdsUsingBitset(docIndex.getSearcher(), questionDocId)
        val childDocAndIds = childDocIds.map { DocWithId(it, docIndex.getSearcher().doc(it)) }
        val indexedSite = indexedSiteIndex.getById(questionDocWithId.doc.get("indexedSiteId")!!)!!
        return QuestionDocs(questionDocWithId, childDocAndIds, indexedSite, queryScore)
    }

    fun getQuestionByUid(uid: String): Question? {
        val docId = docIndex.docIdIndex.getByTerms(mapOf("uid" to uid, "child" to "N"))
        if(docId == null) return null
        return getQuestionDocs(docId).convertToQuestion();
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
        logger.info { "Fetching children took: " + (System.currentTimeMillis() - start) }
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

open class QuestionDocs(
    val questionDoc: DocWithId,
    val childDocs: List<DocWithId>,
    val indexedSite: IndexedSite,
    val queryScore: Float = 0.0f
){
    fun convertToQuestion(): Question{
        val comments = childDocs.map{ it.doc }.filter { it.get("type") == "comment" }.map { Comment(it) }.toList()
        val answers = childDocs.map{ it.doc }.filter { it.get("type") == "answer" }.map { doc -> Answer(doc, comments.filter { it.postUid == doc.get("uid") }) }.toList()
        val question = Question(
            questionDoc.doc,
            indexedSite,
            comments.filter{it.postUid == questionDoc.doc.get("uid")},
            answers.filter{it.parentUid == questionDoc.doc.get("uid")}
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

class QuestionSummary (
    val siteDomain: String?,
    val uid: String,
    val title: String?,
    val createdDate: String?,
    val numberOfAnswers: Int,
    val tags: String?,
    val score: String?,
    val searchResultText: String,
    val queryScore: Float)

class SearchResults (
    val questionSummaries: List<QuestionSummary>,
    val maxScore: Float,
    val totalHits: Long )

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

    fun getQuestionSummary(questionDocs: QuestionDocs): QuestionSummary {
        val questionDoc = questionDocs.questionDoc.doc
        val frags = questionDocs.allDocs
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
            summary = questionDoc.get("textContent").substring(0, 120) + "..."
        }

        summary = summary.replace(Regex("^[^\\w]]+"), "").trim()
        if(summary[0].isLowerCase()){
            summary = "..." + summary
        }

        val question = questionDocs.convertToQuestion()
        return QuestionSummary(
            question.indexedSite.seSite.urlDomain,
            question.uid,
            question.title,
            question.creationDate,
            question.answers.size,
            question.tags,
            question.score,
            summary,
            questionDocs.queryScore
        )
    }

    private fun getTextFragmentsForField(doc: DocWithId, fieldName: String): List<TextFragment> {
        val stream = TokenSources.getAnyTokenStream(docIndex.getSearcher().indexReader, doc.docId, fieldName, analyzer)
        val frags = highlighter.getBestTextFragments(stream, doc.doc.get(fieldName), true, 2)
        return frags.toList()
    }
}

