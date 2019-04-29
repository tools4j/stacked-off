package org.tools4j.stacked.index

import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.ReaderUtil
import org.apache.lucene.index.Term
import org.apache.lucene.search.*
import org.apache.lucene.search.BooleanClause.Occur
import org.apache.lucene.search.join.*
import org.junit.jupiter.api.Test
import org.apache.lucene.search.IndexSearcher


internal class QuestionIndexTest {

    @Test
    fun search() {
        val stagingIndexes = createAndLoadStagingIndexes()
        val questionIndex = createQuestionIndex()

        QuestionLinker(stagingIndexes, questionIndex).join()
        val q = questionIndex.queryParser.parse("coffee")
        val indexedSite = stagingIndexes.indexedSiteIndex.getByTinyName("coffeeme")!!
        val parentsFilter = QueryBitSetProducer(TermQuery(Term("child", "N")))
        val childSearchQuery = BooleanQuery.Builder()
        childSearchQuery.add(BooleanClause(q, Occur.MUST))
        childSearchQuery.add(BooleanClause(TermQuery(Term("child", "Y")), Occur.MUST))
        val childQuery = ToParentBlockJoinQuery(childSearchQuery.build(), parentsFilter, ScoreMode.Avg)

        val parentQuery = BooleanQuery.Builder()
        parentQuery.add(BooleanClause(q, Occur.MUST))
        parentQuery.add(BooleanClause(TermQuery(Term("child", "N")), Occur.MUST))

        val childAndParentQuery = BooleanQuery.Builder()
        childAndParentQuery.add(BooleanClause(childQuery, Occur.SHOULD))
        childAndParentQuery.add(BooleanClause(parentQuery.build(), Occur.SHOULD))

        val reader = DirectoryReader.open(questionIndex.docIndex.index)
        CheckJoinIndex.check(reader, parentsFilter)

        val indexSearcher = IndexSearcher(reader)
        val docs = indexSearcher.search(childAndParentQuery.build(), 10)
        val hits = docs.scoreDocs
        val results = hits.map { indexSearcher.doc(it.doc) }
        println("Hits: ${results.size}")
        println(results.map { it.getField("type") }.joinToString("\n"))

        println("Attempting first method of getting child docs...")
        docs.scoreDocs.forEach{ scoreDoc ->
            println("Parent doc: ${scoreDoc.doc}")
            println(getChildDocsUsingQuery(parentsFilter, scoreDoc.doc, indexSearcher).map { "    " + it.get("type") + ":" + it.get("id") }.joinToString("\n"))
        }

        println("Attempting second method of getting child docs...")

        val questions = ArrayList<Question>()
        docs.scoreDocs.forEach{ scoreDoc ->
            println("Parent doc: ${scoreDoc.doc}")
            val parentDoc = indexSearcher.doc(scoreDoc.doc)
            val childDocs = getChildDocsUsingBitset(indexSearcher, parentsFilter, scoreDoc.doc)
            println(childDocs.map { "    " + it.get("type") + ":" + it.get("id") }.joinToString("\n"))
            questions.add(ParentAndChildDocs(indexedSite, parentDoc, childDocs).convert().convert())
        }
        println(questions.map { it.toPrettyString() }.joinToString("\n"))
    }

    private fun getChildDocsUsingQuery(
        parentsFilter: QueryBitSetProducer,
        parentDocId: Int,
        indexSearcher: IndexSearcher
    ): List<Document> {
        val start = System.currentTimeMillis()
        val childrenQuery = ParentChildrenBlockJoinQuery(parentsFilter, MatchAllDocsQuery(), parentDocId.toInt())
        val matchingChildren = indexSearcher.search(childrenQuery, 1000)
        val childrenDocs = matchingChildren.scoreDocs.map { indexSearcher.doc(it.doc) }
        val parentDoc = indexSearcher.doc(parentDocId.toInt())
        println("Took: " + (System.currentTimeMillis() - start))
        return childrenDocs
    }

    fun getChildDocsUsingBitset(indexSearcher: IndexSearcher, parentFilter: BitSetProducer, parentDocId: Int): List<Document> {
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
        val prevParent = parentFilter.getBitSet(leaf).prevSetBit(localParentDocId - 1)
        for (childDoc in prevParent + 1 until localParentDocId) {
            childDocs.add(indexReader.document(leaf.docBase + childDoc))
        }
        println("Took: " + (System.currentTimeMillis() - start))
        return childDocs
    }

    private fun getParentDoc(reader: IndexReader, parentFilter: BitSetProducer, childDocID: Int): Document {
        val leaves = reader.leaves()
        val subIndex = ReaderUtil.subIndex(childDocID, leaves)
        val leaf = leaves[subIndex]
        val bits = parentFilter.getBitSet(leaf)
        return leaf.reader().document(bits.nextSetBit(childDocID - leaf.docBase))
    }
}

class ParentAndChildDocs(val indexedSite: IndexedSite, val parentDoc: Document, val childDocs: List<Document>){
    fun convert(): QuestionAnswersAndComments{
        val question = RawPostImpl(parentDoc)
        val answers = childDocs.filter { it.get("type") == "answer" }.map { RawPostImpl(it) }.toList()
        val comments = childDocs.filter { it.get("type") == "comment" }.map { RawCommentImpl(it) }.toList()
        return QuestionAnswersAndComments(indexedSite, question, answers, comments);
    }
}

class QuestionAnswersAndComments(
    val indexedSite: IndexedSite,
    val question: RawPost,
    val answers: List<RawPost>,
    val comments: List<RawComment>){

    fun convert(): Question{
        val returnAnswers = answers.map { post -> PostImpl(post, null, comments.filter { it.postUid == post.uid }.map { CommentImpl(it, null) }) }
        val returnQuestion = QuestionImpl(
            PostImpl(
                question,
                null,
                comments.filter { it.postUid == question.uid }.map { CommentImpl(it, null) }),
            indexedSite,
            returnAnswers);
        return returnQuestion;
    }
}

