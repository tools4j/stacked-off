package org.tools4j.stacked.index

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StoredField
import org.apache.lucene.document.StringField
import javax.xml.namespace.QName
import javax.xml.stream.events.StartElement

interface User {
    val uid: String
    val indexedSiteId: String
    val reputation: String?
    val displayName: String?
    val accountId: String?
    fun convertToDocument(): Document
}

data class UserImpl(
    private val id: String,
    override val indexedSiteId: String,
    override val reputation: String?,
    override val displayName: String?,
    override val accountId: String?) : User {

    constructor(doc: Document): this(
        doc.get("id"),
        doc.get("indexedSiteId"),
        doc.get("reputation"),
        doc.get("displayName"),
        doc.get("accountId"))
    
    override val uid: String
        get() = "$indexedSiteId.$id"

    override fun convertToDocument(): Document {
        val doc = Document()
        doc.add(StringField("uid", uid, Field.Store.NO))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        doc.add(StringField("id", id, Field.Store.YES))
        if(reputation != null) doc.add(StoredField("reputation", reputation))
        if(displayName != null) doc.add(StoredField("displayName", displayName))
        if(accountId != null) doc.add(StoredField("accountId", accountId))
        return doc
    }
}

class UserXmlRowHandler(delegateProvider: () -> ItemHandler<User>): XmlRowHandler<User>(delegateProvider) {
    override fun handle(element: StartElement, indexedSiteId: String) {
        val user = UserImpl(
            element.getAttributeByName(QName.valueOf("Id"))!!.value,
            indexedSiteId,
            element.getAttributeByName(QName.valueOf("Reputation"))?.value,
            element.getAttributeByName(QName.valueOf("DisplayName"))?.value,
            element.getAttributeByName(QName.valueOf("AccountId"))?.value
        )
        delegate.handle(user)
    }
}