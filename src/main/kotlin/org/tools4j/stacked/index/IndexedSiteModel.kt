package org.tools4j.stacked.index

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StoredField
import org.apache.lucene.document.StringField
import java.util.concurrent.atomic.AtomicLong

interface IndexedSite{
    val indexedSiteId: String
    val dateTimeIndexed: String
    val success: Boolean
    val errorMessage: String?
    val seSite: SeSite
    fun convertToDocument(): Document
    fun reIndex(dateTimeIndexed: String, seSite: SeSite): IndexingSite
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
        doc.get("uid"),
        doc.get("dateTimeIndexed"),
        doc.get("success").toBoolean(),
        doc.get("errorMessage"),
        SeSiteImpl(doc))

    override fun reIndex(dateTimeIndexed: String, seSite: SeSite): IndexingSite {
        return IndexingSiteImpl(indexedSiteId, dateTimeIndexed, seSite )
    }

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

class IndexingSiteImpl(
    override val indexedSiteId: String,
    override val dateTimeIndexed: String,
    override val seSite: SeSite) : IndexingSite {

    override fun finished(success: Boolean, errorMessage: String?): IndexedSite {
        return IndexedSiteImpl(indexedSiteId, dateTimeIndexed, success, errorMessage, seSite )
    }
}

interface IndexedSiteIdGenerator{
    fun getNext(): String
}

class IncrementalIndexedSiteIdGenerator(firstValue: Long): IndexedSiteIdGenerator{
    constructor(): this(1L)

    private val nextId = AtomicLong(firstValue)

    override fun getNext(): String {
        return nextId.getAndIncrement().toString()
    }
}