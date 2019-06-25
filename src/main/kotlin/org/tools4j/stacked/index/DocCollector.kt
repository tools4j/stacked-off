package org.tools4j.stacked.index

import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.LeafReaderContext
import org.apache.lucene.search.*
import java.util.*

interface DocCollector {
    fun search(searcher: IndexSearcher, query: Query): Docs
}

open class TopNCollector(val size: Int, val provideExplainPlans: Boolean = false): DocCollector {
    override fun search(searcher: IndexSearcher, query: Query): Docs {
        val topDocs = searcher.search(query, size)
        val explainPlans = if(provideExplainPlans) topDocs.scoreDocs.toList().map { searcher.explain(query, it.doc) } else emptyList()
        return Docs.create(topDocs, explainPlans)
    }
}

class GetMaxSizeCollector(): TopNCollector(1024*1000)

class FindFirstInTopNCollector(val fetchSize: Int, val filter: (ScoreDoc) -> Boolean): DocCollector{
    override fun search(searcher: IndexSearcher, query: Query): Docs {
        val topDocs = searcher.search(query, fetchSize)
        try {
            val firstScoreDoc = topDocs.scoreDocs.first(filter)
            val explainPlans = listOf(searcher.explain(query, firstScoreDoc.doc))
            val topDocs = TopDocs(topDocs.totalHits, listOf(firstScoreDoc).toTypedArray(), topDocs.maxScore)
            return Docs.create(topDocs, explainPlans)
        } catch(e: NoSuchElementException) {
            val topDocs = TopDocs(topDocs.totalHits, emptyArray(), topDocs.maxScore)
            return Docs.create(topDocs, emptyList())
        }
    }
}

class RangeCollector(val fromDocIndexInclusive: Int, val toDocIndexExclusive: Int, val provideExplainPlans: Boolean = false): DocCollector{
    override fun search(searcher: IndexSearcher, query: Query): Docs {
        val topTocsToDocIndexExclusive = searcher.search(query, toDocIndexExclusive)
        val lastDocs = topTocsToDocIndexExclusive.scoreDocs.toList().takeLast(toDocIndexExclusive - fromDocIndexInclusive)
        val topDocs = TopDocs(topTocsToDocIndexExclusive.totalHits, lastDocs.toTypedArray(), topTocsToDocIndexExclusive.maxScore)
        val explainPlans = if(provideExplainPlans) topDocs.scoreDocs.toList().map { searcher.explain(query, it.doc) } else emptyList()
        return Docs.create(topDocs, explainPlans)
    }
}

class UnscoredCollector(val provideExplainPlans: Boolean = false): DocCollector{
    override fun search(searcher: IndexSearcher, query: Query): Docs {
        val unscoredCollector = UnscoredSimpleCollector(searcher.indexReader)
        searcher.search(query, unscoredCollector)
        val scoreDocs = unscoredCollector.docIds.map { ScoreDoc(it, 0.0f) }
        val topDocs = TopDocs(scoreDocs.size.toLong(), scoreDocs.toTypedArray(), 0.0f)
        val explainPlans = if(provideExplainPlans) topDocs.scoreDocs.toList().map { searcher.explain(query, it.doc) } else emptyList()
        return Docs.create(topDocs, explainPlans)
    }
}

private class UnscoredSimpleCollector(val indexReader: IndexReader) : SimpleCollector() {
    private var currentLeafReaderContext: LeafReaderContext? = null
    val docIds = ArrayList<Int>()

    override fun needsScores(): Boolean {
        return false
    }

    override fun doSetNextReader(context: LeafReaderContext?) {
        currentLeafReaderContext = context
    }

    override fun collect(unbasedDocId: Int) {
        docIds.add(currentLeafReaderContext!!.docBase + unbasedDocId)
    }
}
