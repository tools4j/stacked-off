package org.tools4j.stacked

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
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.Directory

abstract class AbstractIndex<T>(val indexFactory: IndexFactory, val name: String) {
    internal lateinit var index: Directory
    private lateinit var analyzer: Analyzer
    private lateinit var queryParser: QueryParser

    fun init() {
        index = indexFactory.createIndex(name)
        analyzer = StandardAnalyzer()
        val fields: MutableMap<String, Float> = getIndexedFieldsAndRankings()
        queryParser = MultiFieldQueryParser(
            fields.keys.toTypedArray(),
            analyzer,
            fields
        );
    }

    fun addItems(items: List<T>){
        val config = IndexWriterConfig(analyzer)
        val w = IndexWriter(index, config)
        for (item in items) {
            w.addDocument(convertItemToDocument(item))
        }
        w.close()
    }

    fun getItemHandler(): ItemHandler<T>{
        return object: ItemHandler<T>{
            val config = IndexWriterConfig(analyzer)
            val w = IndexWriter(index, config)

            override fun handle(item: T) {
                w.addDocument(convertItemToDocument(item))
            }

            override fun onFinish() {
                w.close()
            }
        }
    }

    fun search(queryString: String, hitsPerPage: Int = 10): List<T> {
        val q = queryParser.parse(queryString);

        val reader = DirectoryReader.open(index)
        val searcher = IndexSearcher(reader)
        val docs = searcher.search(q, hitsPerPage)
        val hits = docs.scoreDocs

        println("Found " + docs.totalHits + " hits.")
        return hits.map{searcher.doc(it.doc)}.map{convertDocumentToItem(it)}.toList()
    }

    fun getById(id: String): T? {
        val reader = DirectoryReader.open(index)
        val searcher = IndexSearcher(reader)
        val docs = searcher.search(TermQuery(Term("id", id)), 1)
        val hits = docs.scoreDocs
        if (docs.totalHits > 0) {
            return convertDocumentToItem(searcher.doc(hits.get(0).doc));
        }
        return null
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