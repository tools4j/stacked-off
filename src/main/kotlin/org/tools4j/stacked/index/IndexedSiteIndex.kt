package org.tools4j.stacked.index

import org.apache.lucene.document.Document
import org.apache.lucene.index.Term

class IndexedSiteIndex(indexFactory: IndexFactory)
    : TypedIndex<IndexedSite>(indexFactory,"sites") {

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> = LinkedHashMap()

    override fun convertDocumentToItem(doc: Document): IndexedSite = IndexedSiteImpl(doc)

    override fun convertItemToDocument(indexedSite: IndexedSite): Document = indexedSite.convertToDocument()

    fun getHighestIndexedSiteId(): Long {
        return getAll().map { indexedSite -> indexedSite.indexedSiteId.toLong() }.max() ?: 0
    }

    fun getMatching(seSite: SeSite): List<IndexedSite> {
        return getAll().filter { seSite.fuzzyMatches(it.seSite) }.toList()
    }

    fun getByTinyName(tinyName: String): IndexedSite? {
        return getByTerm(Term("tinyName", tinyName))
    }

    fun purgeSites(indexedSiteIds: List<String>) {
        for (indexedSiteId in indexedSiteIds) {
            purgeSite(indexedSiteId)
        }
    }
}