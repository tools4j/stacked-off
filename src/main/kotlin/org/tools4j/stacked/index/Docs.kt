package org.tools4j.stacked.index

import org.apache.lucene.search.Explanation
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopDocs

class Docs(val topDocs: List<Doc>, val totalHits: Long, val maxScore: Float): List<Doc> by topDocs{
    companion object {
        @JvmStatic
        fun create(topDocs: TopDocs, explains: List<Explanation> = emptyList()): Docs {
            val docs = ArrayList<Doc>()
            for(i in 0..(topDocs.scoreDocs.size - 1)){
                docs.add(Doc(topDocs.scoreDocs[i], if(explains.isEmpty()) null else explains[i]))
            }
            return Docs(docs, topDocs.totalHits, topDocs.maxScore)
        }
    }
}

class Doc(val score: Float, val docId: Int, val shardIndex: Int = 0, val explanation: Explanation? = null){
    constructor(scoreDoc: ScoreDoc, explanation: Explanation? = null): this(scoreDoc.score, scoreDoc.doc, scoreDoc.shardIndex, explanation );
}