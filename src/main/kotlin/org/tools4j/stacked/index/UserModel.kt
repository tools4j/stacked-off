package org.tools4j.stacked.index

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StoredField
import org.apache.lucene.document.StringField
import javax.xml.namespace.QName
import javax.xml.stream.events.StartElement

data class StagingUser(
    val id: String,
    val reputation: String?,
    val displayName: String?,
    val accountId: String?) {

    constructor(doc: Document): this(
        doc.get("id"),
        doc.get("reputation"),
        doc.get("displayName"),
        doc.get("accountId"))

    fun convertToDocument(): Document {
        val doc = Document()
        doc.add(StringField("id", id, Field.Store.YES))
        if(reputation != null) doc.add(StoredField("reputation", reputation))
        if(displayName != null) doc.add(StoredField("displayName", displayName))
        if(accountId != null) doc.add(StoredField("accountId", accountId))
        return doc
    }

}

class UserXmlRowHandler(delegate: ItemHandler<StagingUser>): XmlRowHandler<StagingUser>(delegate) {
    override fun handle(element: StartElement) {
        val user = StagingUser(
            element.getAttributeByName(QName.valueOf("Id"))!!.value,
            element.getAttributeByName(QName.valueOf("Reputation"))?.value,
            element.getAttributeByName(QName.valueOf("DisplayName"))?.value,
            element.getAttributeByName(QName.valueOf("AccountId"))?.value
        )
        delegate.handle(user)
    }
}

interface ContainsPrimaryUserFields {
    val userUid: String?
    val userReputation: String?
    val userDisplayName: String?
}
