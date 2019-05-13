package org.tools4j.stacked.index

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StoredField
import org.apache.lucene.document.StringField

interface IndexedSite{
    val indexedSiteId: String
    val dateTimeIndexed: String
    val status: Status
    val errorMessage: String?
    val seSite: SeSite
    fun convertToDocument(): Document
    fun withStatus(status: Status, errorMessage: String? = null): IndexedSite
}

interface IndexingSite{
    val indexedSiteId: String
    val dateTimeIndexed: String
    val seSite: SeSite
    fun finished(status: String, errorMessage: String?): IndexedSite
}

class IndexedSiteImpl(
    override val indexedSiteId: String,
    override val dateTimeIndexed: String,
    override val status: Status,
    override val errorMessage: String?,
    override val seSite: SeSite) : IndexedSite {

    constructor(doc: Document): this(
        doc.get("id"),
        doc.get("dateTimeIndexed"),
        Status.valueOf(doc.get("status")),
        doc.get("errorMessage"),
        SeSiteImpl(doc))

    override fun convertToDocument(): Document {
        val doc = Document()
        doc.add(StringField("id", indexedSiteId, Field.Store.YES))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        doc.add(StoredField("dateTimeIndexed", dateTimeIndexed))
        doc.add(StringField("status", status.name, Field.Store.YES))
        if(errorMessage != null) doc.add(StoredField("errorMessage", errorMessage))
        seSite.addTo(doc)
        return doc
    }

    override fun toString(): String {
        return "IndexedSiteImpl(indexedSiteId='$indexedSiteId', dateTimeIndexed='$dateTimeIndexed', status=$status, errorMessage=$errorMessage, seSite=$seSite)"
    }

    override fun withStatus(status: Status, errorMessage: String?): IndexedSite {
        return IndexedSiteImpl(indexedSiteId, dateTimeIndexed, status, errorMessage, seSite)
    }
}

enum class Status {
    LOADING_STAGING_INDICES,
    LINKING_STAGING_INDICES,
    ERROR,
    LOADED
}

interface IndexedSiteIdGenerator{
    fun getNext(): String
}
