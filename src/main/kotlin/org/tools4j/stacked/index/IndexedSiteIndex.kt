package org.tools4j.stacked.index

import org.apache.lucene.document.Document

class IndexedSiteIndex(indexFactory: IndexFactory)
    : AbstractIndex<IndexedSite>(indexFactory,"sites") {

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> = LinkedHashMap()

    override fun convertDocumentToItem(doc: Document): IndexedSite = IndexedSiteImpl(doc)

    override fun convertItemToDocument(indexedSite: IndexedSite): Document = indexedSite.convertToDocument()

    fun getHighestIndexedSiteId(): Long {
        return getAll().map { indexedSite -> indexedSite.indexedSiteId.toLong() }.max() ?: 0
    }
}