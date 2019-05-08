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



class DocIdIndex(val index: Directory, val name: String) {
    companion object: KLogging()

    fun getByTerm(term: Term): Int? {
        return getByQuery(TermQuery(term))
    }

    fun getByTerms(terms: Map<String, String>): Int? {
        val queryBuilder = BooleanQuery.Builder()
        for (e in terms.entries) {
            queryBuilder.add(TermQuery(Term(e.key, e.value)), BooleanClause.Occur.MUST)
        }
        return getByQuery(queryBuilder.build())
    }

    fun getByQuery(query: Query): Int? {
        val searcher = IndexSearcher(DirectoryReader.open(index))
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

    fun searchByTerm(term: Term): List<Int> {
        return searchByQuery(TermQuery(term))
    }

    fun searchByTerm(key: String, value: String): List<Int> {
        return searchByTerm(Term(key, value))
    }

    fun searchAllTermsMustMatch(terms: List<Term>): List<Int> {
        return searchByTerms(terms, BooleanClause.Occur.MUST)
    }

    fun searchAllTermsMustMatch(terms: Map<String, String>): List<Int> {
        return searchByTerms(terms, BooleanClause.Occur.MUST)
    }

    fun searchByTerms(terms: Map<String, String>, booleanClause: BooleanClause.Occur): List<Int> {
        return searchByTerms(terms.entries.map { Term(it.key, it.value) }.toList(), booleanClause)
    }

    fun searchByTerms(terms: List<Term>, booleanClause: BooleanClause.Occur): List<Int> {
        val query = BooleanQuery.Builder()
        for (term in terms) {
            query.add(TermQuery(term), booleanClause)
        }
        return searchByQuery(query.build())
    }

    fun searchAnyTermsCanMatch(terms: Map<String, String>): List<Int> {
        return searchByTerms(terms, BooleanClause.Occur.SHOULD)
    }

    fun searchByQuery(query: Query): List<Int> {
        val searcher = IndexSearcher(DirectoryReader.open(index))
        val docs = searcher.search(query, 10000)
        val hits = docs.scoreDocs
        return hits.map{it.doc}.toList()
    }

    fun getAll(): List<Int> {
        val searcher = IndexSearcher(DirectoryReader.open(index))
        val docs = searcher.search(MatchAllDocsQuery(), 10000)
        val hits = docs.scoreDocs
        logger.debug{ "Found " + docs.totalHits + " total records found." }
        return hits.map{it.doc}.toList()
    }

    fun search(searchLambda: (IndexSearcher)-> TopDocs): List<Int> {
        val reader = DirectoryReader.open(index)
        val searcher = IndexSearcher(reader)
        val docs = searchLambda(searcher)
        val hits = docs.scoreDocs
        if (docs.totalHits > 0) {
            return hits
                .map{it.doc}
                .toList()
        }
        return emptyList()
    }
}