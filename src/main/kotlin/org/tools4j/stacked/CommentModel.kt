package org.tools4j.stacked

import javax.xml.namespace.QName
import javax.xml.stream.events.StartElement

interface RawComment {
    val id: String?
    val postId: String?
    val score: String?
    val text: String?
    val creationDate: String?
    val userId: String?
}

interface Comment: RawComment {
    val user: User?
}

data class CommentImpl(val rawComment: RawComment, override val user: User?): RawComment by rawComment, Comment

data class RawCommentImpl(
    override val id: String?,
    override val postId: String?,
    override val score: String?,
    override val text: String?,
    override val creationDate: String?,
    override val userId: String?) : RawComment


class CommentXmlRowHandler(delegate: ItemHandler<RawComment>): XmlRowHandler<RawComment>(delegate) {
    override fun getParentElementName(): String {
        return "comments"
    }

    override fun handle(element: StartElement) {
        val rawPost = RawCommentImpl(
            element.getAttributeByName(QName.valueOf("Id"))?.value,
            element.getAttributeByName(QName.valueOf("PostId"))?.value,
            element.getAttributeByName(QName.valueOf("Score"))?.value,
            element.getAttributeByName(QName.valueOf("Text"))?.value,
            element.getAttributeByName(QName.valueOf("CreationDate"))?.value,
            element.getAttributeByName(QName.valueOf("UserId"))?.value
        )
        delegate.handle(rawPost)
    }
}