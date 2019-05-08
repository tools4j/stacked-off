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
import org.apache.lucene.search.join.*


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
        "body" to 7.0f,   //post
        "tags" to 7.0f,   //post
        "text" to 7.0f)  //comment

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
        return search(MatchAllDocsQuery());
    }

    fun search(searchTerm: String): List<Question>{
        return search(queryParser.parse(searchTerm))
    }

    fun search(q: Query): List<Question> {
        val childSearchQuery = BooleanQuery.Builder()
        childSearchQuery.add(BooleanClause(q, BooleanClause.Occur.MUST))
        childSearchQuery.add(BooleanClause(TermQuery(Term("child", "Y")), BooleanClause.Occur.MUST))
        val childQuery = ToParentBlockJoinQuery(childSearchQuery.build(), parentsFilter, ScoreMode.Avg)

        val parentQuery = BooleanQuery.Builder()
        parentQuery.add(BooleanClause(q, BooleanClause.Occur.MUST))
        parentQuery.add(BooleanClause(TermQuery(Term("child", "N")), BooleanClause.Occur.MUST))

        val childAndParentQuery = BooleanQuery.Builder()
        childAndParentQuery.add(BooleanClause(childQuery, BooleanClause.Occur.SHOULD))
        childAndParentQuery.add(BooleanClause(parentQuery.build(), BooleanClause.Occur.SHOULD))

        val reader = DirectoryReader.open(docIndex.index)
        CheckJoinIndex.check(reader, parentsFilter)

        val indexSearcher = IndexSearcher(reader)
        val docs = indexSearcher.search(childAndParentQuery.build(), 10000)
        val questions = ArrayList<Question>()
        docs.scoreDocs.map{it.doc}.forEach{ docId ->
            questions.add(getQuestion(indexSearcher, docId))
        }
        return questions
    }

    private fun getQuestion(
        indexSearcher: IndexSearcher,
        docId: Int
    ): Question {
        val parentDoc = indexSearcher.doc(docId)
        val childDocs = getChildDocsUsingBitset(indexSearcher, docId)
        val indexedSite = indexedSiteIndex.getById(parentDoc.get("indexedSiteId"))!!
        val question = ParentAndChildDocs(indexedSite, parentDoc, childDocs).convertToQuestion()
        return question
    }

    fun getQuestionByUid(uid: String): Question? {
        val docId = docIndex.docIdIndex.getByTerms(mapOf("uid" to uid, "child" to "N"))
        if(docId == null) return null
        val reader = DirectoryReader.open(docIndex.index)
        val indexSearcher = IndexSearcher(reader)
        return getQuestion(indexSearcher, docId)
    }

    private fun getChildDocsUsingQuery(
        parentDocId: Int,
        indexSearcher: IndexSearcher
    ): List<Document> {
        val start = System.currentTimeMillis()
        val childrenQuery = ParentChildrenBlockJoinQuery(parentsFilter, MatchAllDocsQuery(), parentDocId.toInt())
        val matchingChildren = indexSearcher.search(childrenQuery, 1000)
        val childrenDocs = matchingChildren.scoreDocs.map { indexSearcher.doc(it.doc) }
        println("Took: " + (System.currentTimeMillis() - start))
        return childrenDocs
    }

    private fun getChildDocsUsingBitset(
        indexSearcher: IndexSearcher,
        parentDocId: Int
    ): List<Document> {
        val start = System.currentTimeMillis()
        val indexReader = indexSearcher.indexReader
        val leaves = indexReader.leaves()
        val subIndex = ReaderUtil.subIndex(parentDocId, leaves)
        val leaf = leaves[subIndex]
        val localParentDocId = parentDocId - leaf.docBase;
        val childDocs = ArrayList<Document>()
        if (localParentDocId == 0) { // test for parentDoc==0 here to avoid passing -1 to prevSetBit later on
            // not a parent, or parent has no children
            return childDocs
        }
        val prevParent = parentsFilter.getBitSet(leaf).prevSetBit(localParentDocId - 1)
        for (childDoc in prevParent + 1 until localParentDocId) {
            childDocs.add(indexReader.document(leaf.docBase + childDoc))
        }
        logger.info { "Fetching children took: " + (System.currentTimeMillis() - start) }
        return childDocs
    }
}

class ParentAndChildDocs(val indexedSite: IndexedSite, val parentDoc: Document, val childDocs: List<Document>){
    fun convertToQuestion(): Question{
        val comments = childDocs.filter { it.get("type") == "comment" }.map { Comment(it) }.toList()
        val answers = childDocs.filter { it.get("type") == "answer" }.map { doc -> Answer(doc, comments.filter { it.postUid == doc.get("uid") }) }.toList()
        val question = Question(
            parentDoc,
            indexedSite,
            comments.filter{it.postUid == parentDoc.get("uid")},
            answers.filter{it.parentUid == parentDoc.get("uid")}
        )
        return question
    }
}