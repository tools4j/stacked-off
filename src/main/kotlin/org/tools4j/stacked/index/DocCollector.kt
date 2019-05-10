package org.tools4j.stacked.index

import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TopScoreDocCollector

interface DocCollector {
    fun search(searcher: IndexSearcher, query: Query): List<Int>
}

open class TopNCollector(val size: Int): DocCollector{
    override fun search(searcher: IndexSearcher, query: Query): List<Int> {
        val topDocs = searcher.search(query, size)
        return topDocs.scoreDocs.map{it.doc}.toList()
    }
}

class GetMaxSizeCollector(): TopNCollector(1024*1000)

class PageCollector(val pageSize: Int, val pageIndex: Int): DocCollector{
    private val collector = TopScoreDocCollector.create(pageSize)

    override fun search(searcher: IndexSearcher, query: Query): List<Int> {
        val startIndex = pageIndex * pageSize
        searcher.search(query, collector)
        return collector.topDocs(startIndex).scoreDocs.map{it.doc}.toList()
    }
}


