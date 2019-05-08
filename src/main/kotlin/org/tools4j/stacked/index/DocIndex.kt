package org.tools4j.stacked.index

import mu.KLogging
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.search.*
import org.apache.lucene.store.Directory
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.TermQuery



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
        writer = IndexWriter(index, config)
        writer.commit()
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

    fun purgeSite(indexedSiteId: String){
        writer.deleteDocuments(Term("indexedSiteId", indexedSiteId))
        writer.commit()
    }

    fun purge() {
        writer.deleteAll()
        writer.commit()
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

    fun searchByTerm(term: Term): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchByTerm(term))
    }

    fun searchByTerm(key: String, value: String): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchByTerm(key, value))
    }

    fun searchAllTermsMustMatch(terms: List<Term>): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchAllTermsMustMatch(terms))
    }

    fun searchAllTermsMustMatch(terms: Map<String, String>): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchAllTermsMustMatch(terms))
    }

    fun searchByTerms(terms: Map<String, String>, booleanClause: BooleanClause.Occur): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchByTerms(terms, booleanClause))
    }

    fun searchByTerms(terms: List<Term>, booleanClause: BooleanClause.Occur): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchByTerms(terms, booleanClause))
    }

    fun searchAnyTermsCanMatch(terms: Map<String, String>): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchAnyTermsCanMatch(terms))
    }

    fun searchByQuery(query: Query): List<Document> {
        return getDocsFromDocIds(docIdIndex.searchByQuery(query))
    }

    fun getAll(): List<Document> {
        return getDocsFromDocIds(docIdIndex.getAll())
    }

    fun search(searchLambda: (IndexSearcher)-> TopDocs): List<Document> {
        return getDocsFromDocIds(docIdIndex.search(searchLambda))
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
        writer.commit()
    }

    private fun getDocsFromDocIds(docIds: List<Int>): List<Document> {
        if(docIds.isEmpty()) return emptyList()
        val indexSearcher = IndexSearcher(DirectoryReader.open(index))
        return docIds.map { indexSearcher.doc(it) }
    }

    private fun getDocFromDocId(docId: Int?): Document? {
        if(docId == null) return null
        val indexSearcher = IndexSearcher(DirectoryReader.open(index))
        return indexSearcher.doc(docId)
    }
}
