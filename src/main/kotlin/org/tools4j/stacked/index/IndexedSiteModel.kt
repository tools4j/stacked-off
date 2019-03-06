package org.tools4j.stacked.index

import org.apache.lucene.document.*
import java.util.*

interface IndexedSite{
    val indexedSiteId: String
    val dateTimeIndexed: String
    val success: Boolean
    val errorMessage: String?
    val seSite: SeSite
    fun convertToDocument(): Document
}

class IndexedSiteImpl(
    override val indexedSiteId: String,
    override val dateTimeIndexed: String,
    override val success: Boolean,
    override val errorMessage: String?,
    override val seSite: SeSite) : IndexedSite {

    constructor(doc: Document): this(
        doc.get("uid"),
        doc.get("dateTimeIndexed"),
        doc.get("success").toBoolean(),
        doc.get("errorMessage"),
        SeSiteImpl(doc))

    override fun convertToDocument(): Document {
        val doc = Document()
        doc.add(StringField("uid", indexedSiteId, Field.Store.YES))
        doc.add(StoredField("dateTimeIndexed", dateTimeIndexed))
        doc.add(StoredField("success", success.toString()))
        if(errorMessage != null) doc.add(StoredField("errorMessage", errorMessage))
        seSite.addTo(doc)
        return doc
    }
}

interface IndexedSiteIdGenerator{
    fun getNext(): String
}

class GUIDIndexedSiteIdGenerator: IndexedSiteIdGenerator{
    override fun getNext(): String {
        return UUID.randomUUID().toString()
    }
}
