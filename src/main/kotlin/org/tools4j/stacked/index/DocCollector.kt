package org.tools4j.stacked.index

import org.apache.lucene.search.*

interface DocCollector {
    fun search(searcher: IndexSearcher, query: Query): TopDocs
}

open class TopNCollector(val size: Int): DocCollector {
    override fun search(searcher: IndexSearcher, query: Query): TopDocs {
        return searcher.search(query, size)
    }
}

class GetMaxSizeCollector(): TopNCollector(1024*1000)

class PageCollector(val pageSize: Int, val pageIndex: Int): DocCollector{
    private val collector = TopScoreDocCollector.create(pageSize)

    override fun search(searcher: IndexSearcher, query: Query): TopDocs {
        val startIndex = pageIndex * pageSize
        searcher.search(query, collector)
        return collector.topDocs()
    }
}
