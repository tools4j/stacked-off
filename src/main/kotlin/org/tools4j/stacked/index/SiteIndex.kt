package org.tools4j.stacked.index

import org.apache.lucene.document.*
import org.apache.lucene.index.Term
import org.apache.lucene.search.TermQuery

class SiteIndex(indexFactory: IndexFactory)
    : AbstractIndex<Site>(indexFactory, "sites") {

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> {
        return mutableMapOf("tinyName" to 10.0f, "name" to 10.0f, "longName" to 7.0f, "url" to 10.0f)
    }

    override fun convertDocumentToItem(doc: Document): Site = SiteImpl(doc)

    override fun convertItemToDocument(site: Site): Document = site.convertToDocument()
}