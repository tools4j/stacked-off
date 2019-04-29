package org.tools4j.stacked.index

import mu.KLogging
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.join.BitSetProducer
import org.apache.lucene.search.join.CheckJoinIndex
import org.apache.lucene.search.join.ParentChildrenBlockJoinQuery
import org.apache.lucene.search.join.QueryBitSetProducer


class QuestionIndex(indexFactory: IndexFactory): Initializable, Shutdownable {
    val docIndex = DocIndex(indexFactory, "questions");
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

    fun search(queryString: String, hitsPerPage: Int = 10): List<Document> {
        SingleTypedIndex.logger.debug{"Searching with query [$queryString]"}
        val startTimeMs = System.currentTimeMillis()
        val q = queryParser.parse(queryString);
        val reader = DirectoryReader.open(docIndex.index)
        val indexSearcher = IndexSearcher(reader)

        val docs = indexSearcher.search(q, hitsPerPage)
        val hits = docs.scoreDocs
        val endTimeMs = System.currentTimeMillis()
        val durationMs = endTimeMs - startTimeMs
        val results = hits.map { indexSearcher.doc(it.doc) }

        SingleTypedIndex.logger.debug{"Found " + docs.totalHits + " hits. Took $durationMs ms."}
        return results.toList()
    }

    fun addDocsAsBlock(docs: List<Document>){
        docIndex.addDocsAsBlock(docs);
    }

    fun purgeSite(indexedSiteId: String) {
        docIndex.purgeSite(indexedSiteId)
    }
}