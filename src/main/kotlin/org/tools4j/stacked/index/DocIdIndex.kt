package org.tools4j.stacked.index

import mu.KLogging
import org.apache.lucene.document.*
import org.apache.lucene.index.*
import org.apache.lucene.search.*
import org.apache.lucene.store.Directory
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.TermQuery



class DocIdIndex(val index: Directory, val name: String) {
    companion object: KLogging()
    lateinit var reader: IndexReader
    lateinit var searcher: IndexSearcher

    fun init(){
        reader = DirectoryReader.open(index)
        searcher = IndexSearcher(reader)
    }

    fun onNewDataAddedToIndex(){
        reader = DirectoryReader.open(index)
        searcher = IndexSearcher(reader)
    }

    fun getByTerm(term: Term): Int? {
        return getByQuery(TermQuery(term))
    }

    fun getDoc(docId: Int): Document?{
        return searcher.doc(docId)
    }

    fun getByTerms(terms: Map<String, String>): Int? {
        val queryBuilder = BooleanQuery.Builder()
        for (e in terms.entries) {
            queryBuilder.add(TermQuery(Term(e.key, e.value)), BooleanClause.Occur.MUST)
        }
        return getByQuery(queryBuilder.build())
    }

    fun getByQuery(query: Query): Int? {
        val docs = searcher.search(query, 2)
        val hits = docs.scoreDocs
        if (docs.totalHits == 0L) {
            return null
        } else if (docs.totalHits == 1L) {
            return hits.get(0).doc;
        } else {
            throw IllegalStateException("Found more than one document with query [$query] " +
                    "Items:\n${hits.map{searcher.doc(it.doc)}.joinToString("\n")}")
        }
    }

    fun searchByTerm(term: Term, docCollector: DocCollector = GetMaxSizeCollector()): List<Int> {
        return searchByQuery(TermQuery(term), docCollector)
    }

    fun searchByTerm(key: String, value: String, docCollector: DocCollector = GetMaxSizeCollector()): List<Int> {
        return searchByTerm(Term(key, value), docCollector)
    }

    fun searchAllTermsMustMatch(terms: List<Term>, docCollector: DocCollector = GetMaxSizeCollector()): List<Int> {
        return searchByTerms(terms, BooleanClause.Occur.MUST, docCollector)
    }

    fun searchAllTermsMustMatch(terms: Map<String, String>, docCollector: DocCollector = GetMaxSizeCollector()): List<Int> {
        return searchByTerms(terms, BooleanClause.Occur.MUST, docCollector)
    }

    fun searchByTerms(terms: Map<String, String>, booleanClause: BooleanClause.Occur, docCollector: DocCollector = GetMaxSizeCollector()): List<Int> {
        return searchByTerms(terms.entries.map { Term(it.key, it.value) }.toList(), booleanClause, docCollector)
    }

    fun searchAnyTermsCanMatch(terms: Map<String, String>, docCollector: DocCollector = GetMaxSizeCollector()): List<Int> {
        return searchByTerms(terms, BooleanClause.Occur.SHOULD, docCollector)
    }

    fun searchByTerms(terms: List<Term>, booleanClause: BooleanClause.Occur, docCollector: DocCollector = GetMaxSizeCollector()): List<Int> {
        val chunks = terms.chunked(BooleanQuery.getMaxClauseCount())
        val results = ArrayList<Int>()
        for(chunk in chunks){
            val query = BooleanQuery.Builder()
            for (term in chunk) {
                query.add(TermQuery(term), booleanClause)
            }
            results.addAll(searchByQuery(query.build(), docCollector))
        }
        return results
    }

    fun searchByQuery(query: Query, docCollector: DocCollector = GetMaxSizeCollector()): List<Int> {
        return docCollector.search(searcher, query)
    }

    fun getAll(): List<Int> {
        return GetMaxSizeCollector().search(searcher, MatchAllDocsQuery())
    }

    fun size(): Int {
        return reader.numDocs()
    }
}