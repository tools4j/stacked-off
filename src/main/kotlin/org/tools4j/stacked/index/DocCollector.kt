package org.tools4j.stacked.index

import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.ReaderUtil
import org.apache.lucene.search.*

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
    val docIds = ArrayList<Int>()

    override fun needsScores(): Boolean {
        return false
    }

    override fun collect(unbasedDocId: Int) {
        val leaves = indexReader.leaves()
        val subIndex = ReaderUtil.subIndex(unbasedDocId, leaves)
        val leaf = leaves[subIndex]
        docIds.add(leaf.docBase + unbasedDocId)
    }
}
