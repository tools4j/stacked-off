package org.tools4j.stacked.index

import mu.KLogging
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.store.Directory


class DocIndex(val indexFactory: IndexFactory, val name: String): Initializable, Shutdownable {
    internal lateinit var index: Directory
    private lateinit var analyzer: Analyzer
    private lateinit var writer: IndexWriter
    lateinit var docIdIndex: DocIdIndex
    companion object: KLogging()

    override fun init() {
        index = indexFactory.createIndex(name)
        docIdIndex = DocIdIndex(index, name)
        analyzer = StandardAnalyzer()
        val config = IndexWriterConfig(analyzer)
        config.setMaxBufferedDocs(10_000)
        config.setRAMBufferSizeMB(256.0)
        writer = IndexWriter(index, config)
        writer.commit()
        docIdIndex.init()
    }

    override fun shutdown() {
        writer.close()
    }

    fun addItems(docs: List<Document>){
        for (doc in docs) {
            writer.addDocument(doc)
        }
        writer.commit()
    }

    fun addDoc(doc: Document){
        addItems(listOf(doc))
    }

    fun purge() {
        writer.deleteAll()
        writer.commit()
        onIndexDataChange()
    }

    fun <T> getDocumentHandler(converter: (T) -> Document): ItemHandler<T> {
        return object: ItemHandler<T> {
            override fun handle(item: T) {
                val doc = converter(item)
                writer.addDocument(doc)
            }

            override fun onFinish() {
                writer.commit()
            }
        }
    }

    fun getByTerm(term: Term): Document? {
        return getDocFromDocId(docIdIndex.getByTerm(term))
    }

    fun getByTerms(terms: Map<String, String>): Document? {
        return getDocFromDocId(docIdIndex.getByTerms(terms))

    }

    fun getByQuery(query: Query): Document? {
        return getDocFromDocId(docIdIndex.getByQuery(query))
    }

    fun searchByTerm(term: Term, docCollector: DocCollector = GetMaxSizeCollector()): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchByTerm(term, docCollector))
    }

    fun searchByTerm(key: String, value: String, docCollector: DocCollector = GetMaxSizeCollector()): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchByTerm(key, value, docCollector))
    }

    fun searchAllTermsMustMatch(terms: List<Term>, docCollector: DocCollector = GetMaxSizeCollector()): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchAllTermsMustMatch(terms, docCollector))
    }

    fun searchAllTermsMustMatch(terms: Map<String, String>, docCollector: DocCollector = GetMaxSizeCollector()): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchAllTermsMustMatch(terms, docCollector))
    }

    fun searchByTerms(terms: Map<String, String>, booleanClause: BooleanClause.Occur, docCollector: DocCollector = GetMaxSizeCollector()): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchByTerms(terms, booleanClause, docCollector))
    }

    fun searchByTerms(terms: List<Term>, booleanClause: BooleanClause.Occur, docCollector: DocCollector = GetMaxSizeCollector()): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchByTerms(terms, booleanClause, docCollector))
    }

    fun searchAnyTermsCanMatch(terms: Map<String, String>, docCollector: DocCollector = GetMaxSizeCollector()): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchAnyTermsCanMatch(terms, docCollector))
    }

    fun searchByQuery(query: Query, docCollector: DocCollector = GetMaxSizeCollector()): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchByQuery(query, docCollector))
    }

    fun getAll(): List<Document> {
        return getDocsFromDocIds(docIdIndex.getAll())
    }

    fun forEachDocumentInIndex(worker: (Document, Int, Int) -> Unit){
        val reader = DirectoryReader.open(index)
        val maxDoc = reader.maxDoc()
        for (i in 0 until maxDoc) {
            val doc = reader.document(i)
            worker(doc, i, maxDoc)
        }
    }

    fun forEachElementInIndex(worker: (Document, Int, Int) -> Unit){
        forEachDocumentInIndex { doc, index, totalCount -> worker(doc, index, totalCount) }
    }

    fun addDocsAsBlock(docs: List<Document>) {
        writer.addDocuments(docs)
    }

    fun commit(){
        writer.commit()
    }

    private fun getDocsFromDocIds(docIds: List<Int>): List<Document> {
        if(docIds.isEmpty()) return emptyList()
        return docIds.map { docIdIndex.getDoc(it) }.filterNotNull().toList()
    }

    private fun getDocFromDocId(docId: Int?): Document? {
        if(docId == null) return null
        return docIdIndex.getDoc(docId)
    }

    fun getSearcher(): IndexSearcher {
        return docIdIndex.searcher
    }

    fun onIndexDataChange() {
        docIdIndex.onIndexDataChange()
    }

    fun size(): Int {
        return docIdIndex.size()
    }

    fun deleteDocumentsByTerm(term: Term) {
        writer.deleteDocuments(term)
        writer.commit()
    }
}
