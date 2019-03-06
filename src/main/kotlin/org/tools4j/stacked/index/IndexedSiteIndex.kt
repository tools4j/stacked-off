package org.tools4j.stacked.index

import org.apache.lucene.document.Document

class IndexedSiteIndex(indexFactory: IndexFactory)
    : AbstractIndex<IndexedSite>(indexFactory,"sites") {

    var indexId = 1L

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> = LinkedHashMap()

    override fun convertDocumentToItem(doc: Document): IndexedSite = IndexedSiteImpl(doc)

    override fun convertItemToDocument(indexedSite: IndexedSite): Document = indexedSite.convertToDocument()
}