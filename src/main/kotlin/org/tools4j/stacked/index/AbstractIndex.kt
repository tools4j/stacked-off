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

abstract class AbstractIndex<T>(val indexFactory: IndexFactory, val name: String): Initializable, Shutdownable {
    internal lateinit var index: Directory
    private lateinit var analyzer: Analyzer
    private lateinit var queryParser: QueryParser
    private lateinit var writer: IndexWriter

    override fun init() {
        index = indexFactory.createIndex(name)
        analyzer = StandardAnalyzer()
        val fields: MutableMap<String, Float> = getIndexedFieldsAndRankings()
        queryParser = MultiFieldQueryParser(
            fields.keys.toTypedArray(),
            analyzer,
            fields
        )
        val config = IndexWriterConfig(analyzer)
        writer = IndexWriter(index, config)
        writer.commit()
    }

    override fun shutdown() {
        writer.close()
    }

    fun addItems(items: List<T>){
        for (item in items) {
            val doc = convertItemToDocument(item)
            writer.addDocument(doc)
        }
        writer.commit()
    }

    fun addItem(item: T){
        addItems(listOf(item))
    }

    fun getItemHandler(): ItemHandler<T> {
        return object: ItemHandler<T> {
            override fun handle(item: T) {
                val doc = convertItemToDocument(item)
                writer.addDocument(doc)
            }

            override fun onFinish() {
                writer.commit()
            }
        }
    }

    fun search(queryString: String, hitsPerPage: Int = 10): List<T> {
        val startTimeMs = System.currentTimeMillis()
        val q = queryParser.parse(queryString);
        val reader = DirectoryReader.open(index)
        val indexSearcher = IndexSearcher(reader)
        val docs = indexSearcher.search(q, hitsPerPage)
        val hits = docs.scoreDocs
        val endTimeMs = System.currentTimeMillis()
        val durationMs = endTimeMs - startTimeMs
        println("Found " + docs.totalHits + " hits. Took $durationMs ms.")
        return hits.map{indexSearcher.doc(it.doc)}.map{convertDocumentToItem(it)}.toList()
    }

    fun getByUid(uid: String): T? {
        return getByTerm(Term("uid", uid))
    }

    fun getByTerm(term: Term): T? {
        return getByTermQuery(TermQuery(term))
    }

    fun getByTermQuery(termQuery: TermQuery): T? {
        val searcher = IndexSearcher(DirectoryReader.open(index))
        val docs = searcher.search(termQuery, 2)
        val hits = docs.scoreDocs
        if (docs.totalHits == 0L) {
            return null
        } else if (docs.totalHits == 1L) {
            return convertDocumentToItem(searcher.doc(hits.get(0).doc));
        } else {
            throw IllegalStateException("Found more than one item with term [$termQuery] " +
                    "Items:\n${hits.map{convertDocumentToItem(searcher.doc(it.doc))}.joinToString("\n")}")
        }
    }

    fun searchByTerm(term: Term): List<T> {
        return searchByTermQuery(TermQuery(term))
    }

    fun searchByTerm(key: String, value: String): List<T> {
        return searchByTerm(Term(key, value))
    }

    fun searchByTermQuery(termQuery: TermQuery): List<T> {
        val searcher = IndexSearcher(DirectoryReader.open(index))
        val docs = searcher.search(termQuery, 10000)
        val hits = docs.scoreDocs
        return hits.map{searcher.doc(it.doc)}.map{convertDocumentToItem(it)}.toList()
    }

    fun getAll(): List<T> {
        val searcher = IndexSearcher(DirectoryReader.open(index))
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

    fun purgeSite(indexedSiteId: String){
        writer.deleteDocuments(Term("indexedSiteId", indexedSiteId))
        writer.commit()
    }

    abstract fun getIndexedFieldsAndRankings(): MutableMap<String, Float>;
    abstract fun convertDocumentToItem(doc: Document):T
    abstract fun convertItemToDocument(item: T): Document
}