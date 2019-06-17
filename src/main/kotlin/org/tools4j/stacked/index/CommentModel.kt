package org.tools4j.stacked.index

import org.apache.lucene.document.*
import javax.xml.namespace.QName
import javax.xml.stream.events.StartElement

class StagingComment(
    val id: String,
    val postId: String?,
    val userId: String?,
    val score: String?,
    val text: String?,
    val creationDate: String?){

    constructor(doc: Document): this(
        doc.get("id"),
        doc.get("postId"),
        doc.get("userId"),
        doc.get("score"),
        doc.get("text"),
        doc.get("creationDate")
    )

    fun convertToDocument(): Document {
        val doc = Document()
        doc.add(StringField("id", id, Field.Store.YES))
        if(postId != null) doc.add(StringField("postId", postId, Field.Store.YES))
        if(userId != null) doc.add(StoredField("userId", userId))
        if(score != null) doc.add(StoredField("score", score))
        if(text != null) doc.add(TextField("text", text, Field.Store.YES))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        return doc
    }

    fun convertToDocument(indexedSiteId: String, user: StagingUser?): Document {
        val doc = Document()
        doc.add(StringField("uid", "c$indexedSiteId.$id", Field.Store.YES))
        doc.add(StringField("type", "comment", Field.Store.YES))
        doc.add(StringField("child", "Y", Field.Store.NO))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        if(postId != null) doc.add(StringField("postUid", "p$indexedSiteId.$postId", Field.Store.YES))
        if(score != null) doc.add(StoredField("score", score))
        if(text != null) doc.add(TextField("textContent", text, Field.Store.YES))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        if(userId != null) doc.add(StoredField("userUid", "u$indexedSiteId.$userId"))
        if(user?.reputation != null) doc.add(StoredField("userReputation", user.reputation))
        if(user?.displayName != null) doc.add(StoredField("userDisplayName", user.displayName))
        return doc
    }
}

class CommentXmlRowHandler(delegate: ItemHandler<StagingComment>): XmlRowHandler<StagingComment>(delegate) {
    override fun handle(element: StartElement) {
        val rawPost = StagingComment(
            element.getAttributeByName(QName.valueOf("Id"))!!.value,
            element.getAttributeByName(QName.valueOf("PostId"))?.value,
            element.getAttributeByName(QName.valueOf("UserId"))?.value,
            element.getAttributeByName(QName.valueOf("Score"))?.value,
            element.getAttributeByName(QName.valueOf("Text"))?.value,
            element.getAttributeByName(QName.valueOf("CreationDate"))?.value
        )
        delegate.handle(rawPost)
    }
}

data class Comment(
    val uid: String,
    val postUid: String?,
    val score: String?,
    val textContent: String?,
    val creationDate: String?,
    override val userUid: String?,
    override val userReputation: String?,
    override val userDisplayName: String?,
    override val userAccountId: String?

) : ContainsPrimaryUserFields{

    constructor(doc: Document): this(
        doc.get("uid"),
        doc.get("postUid"),
        doc.get("score"),
        doc.get("textContent"),
        doc.get("creationDate"),
        doc.get("userUid"),
        doc.get("userReputation"),
        doc.get("userDisplayName"),
        doc.get("userAccountId")
    )

    fun convertToDocument(): Document {
        val doc = Document()
        doc.add(StringField("uid", uid, Field.Store.YES))
        if(postUid != null) doc.add(StringField("postUid", postUid, Field.Store.YES))
        if(score != null) doc.add(StoredField("score", score))
        if(textContent != null) doc.add(TextField("textContent", textContent, Field.Store.YES))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        if(userUid != null) doc.add(StoredField("userUid", userUid))
        if(userReputation != null) doc.add(StoredField("userReputation", userReputation))
        if(userDisplayName != null) doc.add(StoredField("userDisplayName", userDisplayName))
        if(userAccountId != null) doc.add(StoredField("userAccountId", userAccountId))
        return doc
    }
}
