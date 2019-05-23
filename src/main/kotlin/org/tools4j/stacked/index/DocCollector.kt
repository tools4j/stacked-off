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

class RangeCollector(val fromDocIndexInclusive: Int, val toDocIndexExclusive: Int): DocCollector{
    override fun search(searcher: IndexSearcher, query: Query): TopDocs {
        val topDocs = searcher.search(query, toDocIndexExclusive)
        val lastDocs = topDocs.scoreDocs.toList().takeLast(toDocIndexExclusive - fromDocIndexInclusive)
        return TopDocs(topDocs.totalHits, lastDocs.toTypedArray(), topDocs.maxScore)
    }
}
