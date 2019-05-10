package org.tools4j.stacked.index

import mu.KLogging
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.*
import org.apache.lucene.search.BooleanClause

abstract class TypedIndex<T>(val indexFactory: IndexFactory, val name: String): Initializable, Shutdownable {
    private val docIndex = DocIndex(indexFactory, name);
    private lateinit var analyzer: Analyzer
    private lateinit var queryParser: QueryParser
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

    fun addItems(items: List<T>){
        docIndex.addItems(items.map { convertItemToDocument(it) })
        docIndex.onNewDataAddedToIndex()
    }

    fun addItem(item: T){
        docIndex.addItems(listOf(convertItemToDocument(item)))
        docIndex.onNewDataAddedToIndex()
    }

    fun getItemHandler(): ItemHandler<T> {
        return docIndex.getDocumentHandler<T>{doc -> convertItemToDocument(doc)}
    }

    fun getById(id: String): T? {
        return getByTerm(Term("id", id))
    }

    fun getByIds(uids: List<String>): List<T> {
        return searchByTerms(uids.map { Term("id", it) }.toList(), BooleanClause.Occur.SHOULD)
    }

    fun getByTerm(term: Term): T? {
        return convertDocumentToItemOrNull(docIndex.getByTerm(term))
    }

    fun getByTerms(terms: Map<String, String>): T? {
        return convertDocumentToItemOrNull(docIndex.getByTerms(terms))
    }

    fun getByQuery(query: Query): T? {
        return convertDocumentToItemOrNull(docIndex.getByQuery(query))
    }

    fun searchByTerm(term: Term, docCollector: DocCollector = GetMaxSizeCollector()): List<T> {
        return docIndex.searchByTerm(term, docCollector).map{ convertDocumentToItem(it) }
    }

    fun searchByTerm(key: String, value: String, docCollector: DocCollector = GetMaxSizeCollector()): List<T> {
        return docIndex.searchByTerm(Term(key, value), docCollector).map { convertDocumentToItem(it) }
    }

    fun searchAllTermsMustMatch(terms: List<Term>, docCollector: DocCollector = GetMaxSizeCollector()): List<T> {
        return docIndex.searchAllTermsMustMatch(terms, docCollector).map { convertDocumentToItem(it) }
    }

    fun searchAllTermsMustMatch(terms: Map<String, String>, docCollector: DocCollector = GetMaxSizeCollector()): List<T> {
        return docIndex.searchAllTermsMustMatch(terms, docCollector).map { convertDocumentToItem(it) }
    }

    private fun searchByTerms(terms: Map<String, String>, booleanClause: BooleanClause.Occur, docCollector: DocCollector = GetMaxSizeCollector()): List<T> {
        return docIndex.searchByTerms(terms, booleanClause, docCollector).map {convertDocumentToItem(it)}
    }

    fun searchByTerms(terms: List<Term>, booleanClause: BooleanClause.Occur, docCollector: DocCollector = GetMaxSizeCollector()): List<T> {
        return docIndex.searchByTerms(terms, booleanClause, docCollector).map { convertDocumentToItem(it) }
    }

    fun searchAnyTermsCanMatch(terms: Map<String, String>, docCollector: DocCollector = GetMaxSizeCollector()): List<T> {
        return docIndex.searchAnyTermsCanMatch(terms, docCollector).map { convertDocumentToItem(it) }
    }

    fun searchByQuery(query: Query, docCollector: DocCollector = GetMaxSizeCollector()): List<T> {
        return docIndex.searchByQuery(query, docCollector).map{convertDocumentToItem(it)}.toList()
    }

    fun getAll(): List<T> {
        return docIndex.getAll().map{convertDocumentToItem(it)}.toList()
    }

    fun forEachDocumentInIndex(worker: (Document, Int, Int) -> Unit){
        docIndex.forEachDocumentInIndex(worker)
    }

    fun forEachElementInIndex(worker: (T, Int, Int) -> Unit){
        docIndex.forEachDocumentInIndex { doc, index, total -> worker(convertDocumentToItem(doc), index, total) }
    }

    fun convertDocumentToItemOrNull(doc: Document?): T?{
        return if(doc == null) null else convertDocumentToItem(doc)
    }

    fun purge() {
        docIndex.purge()
    }

    fun purgeSite(indexedSiteId: String){
        docIndex.purgeSite(indexedSiteId)
    }

    fun onNewDataAddedToIndex() {
        docIndex.onNewDataAddedToIndex()
    }

    fun size(): Int {
        return docIndex.size()
    }

    abstract fun getIndexedFieldsAndRankings(): MutableMap<String, Float>;
    abstract fun convertDocumentToItem(doc: Document):T
    abstract fun convertItemToDocument(item: T): Document
}