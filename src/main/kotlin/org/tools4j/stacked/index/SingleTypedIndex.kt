package org.tools4j.stacked.index

import mu.KLogging
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.*
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.TermQuery

abstract class SingleTypedIndex<T>(val indexFactory: IndexFactory, val name: String): Initializable, Shutdownable {
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
    }

    fun addItem(item: T){
        docIndex.addItems(listOf(convertItemToDocument(item)))
    }

    fun getItemHandler(): ItemHandler<T> {
        return docIndex.getDocumentHandler<T>{doc -> convertItemToDocument(doc)}
    }

    fun search(queryString: String, hitsPerPage: Int = 10): List<T> {
        logger.debug{"Searching with query [$queryString]"}
        val startTimeMs = System.currentTimeMillis()
        val q = queryParser.parse(queryString);
        val reader = DirectoryReader.open(docIndex.index)
        val indexSearcher = IndexSearcher(reader)
        val docs = indexSearcher.search(q, hitsPerPage)
        val hits = docs.scoreDocs
        val endTimeMs = System.currentTimeMillis()
        val durationMs = endTimeMs - startTimeMs
        val results = hits.map { indexSearcher.doc(it.doc) }.map { convertDocumentToItem(it) }
        logger.debug{"Found " + docs.totalHits + " hits. Took $durationMs ms."}
        return results.toList()
    }

    fun getByUid(uid: String): T? {
        return convertDocumentToItemOrNull(docIndex.getByUid(uid))
    }

    fun getByUids(uids: List<String>): List<T> {
        return docIndex.searchAllTermsMustMatch(uids.map { Term("uid", it) }).map { convertDocumentToItem(it) }.toList()
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

    fun searchByTerm(term: Term): List<T> {
        return docIndex.searchByTerm(term).map{ convertDocumentToItem(it) }
    }

    fun searchByTerm(key: String, value: String): List<T> {
        return docIndex.searchByTerm(Term(key, value)).map { convertDocumentToItem(it) }
    }

    fun searchAllTermsMustMatch(terms: List<Term>): List<T> {
        return docIndex.searchAllTermsMustMatch(terms).map { convertDocumentToItem(it) }
    }

    fun searchAllTermsMustMatch(terms: Map<String, String>): List<T> {
        return docIndex.searchAllTermsMustMatch(terms).map { convertDocumentToItem(it) }
    }

    private fun searchByTerms(terms: Map<String, String>, booleanClause: BooleanClause.Occur): List<T> {
        return docIndex.searchByTerms(terms, booleanClause).map {convertDocumentToItem(it)}
    }

    fun searchByTerms(terms: List<Term>, booleanClause: BooleanClause.Occur): List<T> {
        return docIndex.searchByTerms(terms, booleanClause).map { convertDocumentToItem(it) }
    }

    fun searchAnyTermsCanMatch(terms: Map<String, String>): List<T> {
        return docIndex.searchAnyTermsCanMatch(terms).map { convertDocumentToItem(it) }
    }

    fun searchByQuery(query: Query): List<T> {
        return docIndex.searchByQuery(query).map{convertDocumentToItem(it)}.toList()
    }

    fun getAll(): List<T> {
        return docIndex.getAll().map{convertDocumentToItem(it)}.toList()
    }

    fun search(searchLambda: (IndexSearcher)-> TopDocs): List<T> {
        return docIndex.search(searchLambda).map { convertDocumentToItem(it) }
    }

    fun purgeSite(indexedSiteId: String){
        docIndex.purgeSite(indexedSiteId)
    }

    fun forEachDocumentInIndex(worker: (Document) -> Unit){
        docIndex.forEachDocumentInIndex(worker)
    }

    fun forEachElementInIndex(worker: (T) -> Unit){
        docIndex.forEachDocumentInIndex { doc -> worker(convertDocumentToItem(doc)) }
    }

    fun convertDocumentToItemOrNull(doc: Document?): T?{
        return if(doc == null) null else convertDocumentToItem(doc)
    }

    abstract fun getIndexedFieldsAndRankings(): MutableMap<String, Float>;
    abstract fun convertDocumentToItem(doc: Document):T
    abstract fun convertItemToDocument(item: T): Document
}