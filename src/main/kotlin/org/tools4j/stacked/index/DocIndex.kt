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
    companion object: KLogging()

    override fun init() {
        index = indexFactory.createIndex(name)
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

    fun getByUid(uid: String): Document? {
        var startTime = System.currentTimeMillis()
        val returnVal = getByTerm(Term("uid", uid))
        SingleTypedIndex.logger.debug { "Getting by uid [$uid] from $name took " + (System.currentTimeMillis() - startTime) + "ms" }
        return returnVal
    }

    fun getByUids(uids: List<String>): List<Document> {
        var startTime = System.currentTimeMillis()
        val returnVal = searchAllTermsMustMatch(uids.map { Term("uid", it) }.toList())
        logger.debug { "Getting by uids [$uids] from $name took " + (System.currentTimeMillis() - startTime) + "ms" }
        return returnVal
    }

    fun getByTerm(term: Term): Document? {
        return getByQuery(TermQuery(term))
    }

    fun getByTerms(terms: Map<String, String>): Document? {
        val queryBuilder = BooleanQuery.Builder()
        for (e in terms.entries) {
            queryBuilder.add(TermQuery(Term(e.key, e.value)), BooleanClause.Occur.MUST)
        }
        return getByQuery(queryBuilder.build())
    }

    fun getByQuery(query: Query): Document? {
        val searcher = IndexSearcher(DirectoryReader.open(index))
        val docs = searcher.search(query, 2)
        val hits = docs.scoreDocs
        if (docs.totalHits == 0L) {
            return null
        } else if (docs.totalHits == 1L) {
            return searcher.doc(hits.get(0).doc);
        } else {
            throw IllegalStateException("Found more than one document with query [$query] " +
                    "Items:\n${hits.map{searcher.doc(it.doc)}.joinToString("\n")}")
        }
    }

    fun searchByTerm(term: Term): List<Document> {
        return searchByQuery(TermQuery(term))
    }

    fun searchByTerm(key: String, value: String): List<Document> {
        return searchByTerm(Term(key, value))
    }

    fun searchAllTermsMustMatch(terms: List<Term>): List<Document> {
        return searchByTerms(terms, BooleanClause.Occur.MUST)
    }

    fun searchAllTermsMustMatch(terms: Map<String, String>): List<Document> {
        return searchByTerms(terms, BooleanClause.Occur.MUST)
    }

    fun searchByTerms(terms: Map<String, String>, booleanClause: BooleanClause.Occur): List<Document> {
        return searchByTerms(terms.entries.map { Term(it.key, it.value) }.toList(), booleanClause)
    }

    fun searchByTerms(terms: List<Term>, booleanClause: BooleanClause.Occur): List<Document> {
        val query = BooleanQuery.Builder()
        for (term in terms) {
            query.add(TermQuery(term), booleanClause)
        }
        return searchByQuery(query.build())
    }

    fun searchAnyTermsCanMatch(terms: Map<String, String>): List<Document> {
        return searchByTerms(terms, BooleanClause.Occur.SHOULD)
    }

    fun searchByQuery(query: Query): List<Document> {
        val searcher = IndexSearcher(DirectoryReader.open(index))
        val docs = searcher.search(query, 10000)
        val hits = docs.scoreDocs
        return hits.map{searcher.doc(it.doc)}.toList()
    }

    fun getAll(): List<Document> {
        val searcher = IndexSearcher(DirectoryReader.open(index))
        val docs = searcher.search(MatchAllDocsQuery(), 10000)
        val hits = docs.scoreDocs
        logger.debug{ "Found " + docs.totalHits + " total records found." }
        return hits.map{searcher.doc(it.doc)}.toList()
    }

    fun search(searchLambda: (IndexSearcher)-> TopDocs): List<Document> {
        val reader = DirectoryReader.open(index)
        val searcher = IndexSearcher(reader)
        val docs = searchLambda(searcher)
        val hits = docs.scoreDocs
        if (docs.totalHits > 0) {
            return hits
                .map{it.doc}
                .map{searcher.doc(it)}
                .toList()
        }
        return emptyList()
    }

    fun purgeSite(indexedSiteId: String){
        writer.deleteDocuments(Term("indexedSiteId", indexedSiteId))
        writer.commit()
    }

    fun forEachDocumentInIndex(worker: (Document) -> Unit){
        val reader = DirectoryReader.open(index)
        for (i in 0 until reader.maxDoc()) {
            val doc = reader.document(i)
            worker(doc)
        }
    }

    fun forEachElementInIndex(worker: (Document) -> Unit){
        forEachDocumentInIndex { doc -> worker(doc) }
    }

    fun addDocsAsBlock(docs: List<Document>) {
        writer.addDocuments(docs)
        writer.commit()
    }
}