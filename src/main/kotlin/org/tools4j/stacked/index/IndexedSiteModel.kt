package org.tools4j.stacked.index

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StoredField
import org.apache.lucene.document.StringField

interface IndexedSite{
    val indexedSiteId: String
    val dateTimeIndexed: String
    val success: Boolean
    val errorMessage: String?
    val seSite: SeSite
    fun convertToDocument(): Document
}

interface IndexingSite{
    val indexedSiteId: String
    val dateTimeIndexed: String
    val seSite: SeSite
    fun finished(success: Boolean, errorMessage: String?): IndexedSite
}

class IndexedSiteImpl(
    override val indexedSiteId: String,
    override val dateTimeIndexed: String,
    override val success: Boolean,
    override val errorMessage: String?,
    override val seSite: SeSite) : IndexedSite {

    constructor(doc: Document): this(
        doc.get("id"),
        doc.get("dateTimeIndexed"),
        doc.get("success").toBoolean(),
        doc.get("errorMessage"),
        SeSiteImpl(doc))

    override fun convertToDocument(): Document {
        val doc = Document()
        doc.add(StringField("id", indexedSiteId, Field.Store.YES))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        doc.add(StoredField("dateTimeIndexed", dateTimeIndexed))
        doc.add(StringField("success", success.toString(), Field.Store.YES))
        if(errorMessage != null) doc.add(StoredField("errorMessage", errorMessage))
        seSite.addTo(doc)
        return doc
    }

    override fun toString(): String {
        return "IndexedSiteImpl(indexedSiteId='$indexedSiteId', dateTimeIndexed='$dateTimeIndexed', success=$success, errorMessage=$errorMessage, seSite=$seSite)"
    }
}

class IndexingSiteImpl(
    override val indexedSiteId: String,
    override val dateTimeIndexed: String,
    override val seSite: SeSite) : IndexingSite {

    constructor(indexedSite: IndexedSite): this(indexedSite.indexedSiteId, indexedSite.dateTimeIndexed, indexedSite.seSite)

    override fun finished(success: Boolean, errorMessage: String?): IndexedSite {
        return IndexedSiteImpl(indexedSiteId, dateTimeIndexed, success, errorMessage, seSite )
    }
}

interface IndexedSiteIdGenerator{
    fun getNext(): String
}
