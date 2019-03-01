package org.tools4j.stacked.index

import org.apache.lucene.document.*
import javax.xml.namespace.QName
import javax.xml.stream.events.StartElement

interface RawComment {
    val uid: String
    val indexedSiteId: String
    val postUid: String?
    val score: String?
    val text: String?
    val creationDate: String?
    val userUid: String?
    fun convertToDocument(): Document
}

interface Comment: RawComment {
    val user: User?
}

data class CommentImpl(val rawComment: RawComment, override val user: User?): RawComment by rawComment,
    Comment

data class RawCommentImpl(
    private val id: String,
    override val indexedSiteId: String,
    private val postId: String?,
    override val score: String?,
    override val text: String?,
    override val creationDate: String?,
    private val userId: String?) : RawComment{

    constructor(doc: Document): this(
        doc.get("id"),
        doc.get("indexedSiteId"),
        doc.get("postId"),
        doc.get("score"),
        doc.get("text"),
        doc.get("creationDate"),
        doc.get("userId")
    )

    override val postUid: String?
        get() = if(postId == null) null else "$indexedSiteId.$postId"

    override val userUid: String?
        get() = if(userId == null) null else "$indexedSiteId.$userId"

    override val uid: String
        get() = "$indexedSiteId.$id"

    override fun convertToDocument(): Document {
        val doc = Document()
        doc.add(StoredField("id", id))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        doc.add(StringField("uid", uid, Field.Store.NO))
        if(postId != null) doc.add(StoredField("postId", postId))
        if(postUid != null) doc.add(StringField("postUid", postUid, Field.Store.NO))
        if(score != null) doc.add(StoredField("score", score))
        if(text != null) doc.add(TextField("text", text, Field.Store.YES))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        if(userId != null) doc.add(StoredField("userId", userId))
        return doc
    }
}

class CommentXmlRowHandler(delegate: ItemHandler<RawComment>): XmlRowHandler<RawComment>(delegate) {
    override fun getParentElementName(): String {
        return "comments"
    }

    override fun handle(element: StartElement, indexedSiteId: String) {
        val rawPost = RawCommentImpl(
            element.getAttributeByName(QName.valueOf("Id"))!!.value,
            indexedSiteId,
            element.getAttributeByName(QName.valueOf("PostId"))?.value,
            element.getAttributeByName(QName.valueOf("Score"))?.value,
            element.getAttributeByName(QName.valueOf("Text"))?.value,
            element.getAttributeByName(QName.valueOf("CreationDate"))?.value,
            element.getAttributeByName(QName.valueOf("UserId"))?.value
        )
        delegate.handle(rawPost)
    }
}