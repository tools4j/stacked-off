package org.tools4j.stacked.index

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.MatchAllDocsQuery
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.Directory

abstract class AbstractIndex<T>(val indexFactory: IndexFactory, val name: String) {
    internal lateinit var index: Directory
    private lateinit var analyzer: Analyzer
    private lateinit var queryParser: QueryParser
    private val searcher: IndexSearcher by lazy {
        val reader = DirectoryReader.open(index)
        IndexSearcher(reader)
    }

    fun init() {
        index = indexFactory.createIndex(name)
        analyzer = StandardAnalyzer()
        val fields: MutableMap<String, Float> = getIndexedFieldsAndRankings()
        queryParser = MultiFieldQueryParser(
            fields.keys.toTypedArray(),
            analyzer,
            fields
        )
    }

    fun addItems(items: List<T>){
        val config = IndexWriterConfig(analyzer)
        val w = IndexWriter(index, config)
        for (item in items) {
            val doc = convertItemToDocument(item)
            w.addDocument(doc)
        }
        w.close()
    }

    fun getItemHandler(): ItemHandler<T> {
        return object: ItemHandler<T> {
            val config = IndexWriterConfig(analyzer)
            val w = IndexWriter(index, config)

            override fun handle(item: T) {
                val doc = convertItemToDocument(item)
                w.addDocument(doc)
            }

            override fun onFinish() {
                w.close()
            }
        }
    }

    fun search(queryString: String, hitsPerPage: Int = 10): List<T> {
        val startTimeMs = System.currentTimeMillis()
        val q = queryParser.parse(queryString);
        val docs = searcher.search(q, hitsPerPage)
        val hits = docs.scoreDocs
        val endTimeMs = System.currentTimeMillis()
        val durationMs = endTimeMs - startTimeMs
        println("Found " + docs.totalHits + " hits. Took $durationMs ms.")
        return hits.map{searcher.doc(it.doc)}.map{convertDocumentToItem(it)}.toList()
    }

    fun getByUid(uid: String): T? {
        val docs = searcher.search(TermQuery(Term("uid", uid)), 2)
        val hits = docs.scoreDocs
        if (docs.totalHits == 0L) {
            return null
        } else if(docs.totalHits == 1L) {
            return convertDocumentToItem(searcher.doc(hits.get(0).doc));
        } else {
            throw IllegalStateException("Found more than one item with uid [$uid]. Items: ${hits.map{it.doc}}")
        }
    }

    fun getAll(): List<T> {
        val docs = searcher.search(MatchAllDocsQuery(), 10000)
        val hits = docs.scoreDocs
        println("Found " + docs.totalHits + " total records found.")
        return hits.map{searcher.doc(it.doc)}.map{convertDocumentToItem(it)}.toList()
    }

    fun search(searchLambda: (IndexSearcher)-> TopDocs): List<T> {
        val reader = DirectoryReader.open(index)
        val searcher = IndexSearcher(reader)
        val docs = searchLambda(searcher)
        val hits = docs.scoreDocs
        if (docs.totalHits > 0) {
            return hits
                .map{it.doc}
                .map{searcher.doc(it)}
                .map{convertDocumentToItem(it)}
                .toList()
        }
        return emptyList()
    }

    abstract fun getIndexedFieldsAndRankings(): MutableMap<String, Float>;
    abstract fun convertDocumentToItem(doc: Document):T
    abstract fun convertItemToDocument(item: T): Document
}