package org.tools4j.stacked

import java.util.function.Consumer
import javax.xml.namespace.QName
import javax.xml.stream.events.StartElement

interface Comment {
    val id: String?
    val postId: String?
    val score: String?
    val text: String?
    val creationDate: String?
    val userId: String?
}

data class CommentImpl(
    override val id: String?,
    override val postId: String?,
    override val score: String?,
    override val text: String?,
    override val creationDate: String?,
    override val userId: String?) : Comment


class CommentXmlRowHandler(delegate: ItemHandler<Comment>): XmlRowHandler<Comment>(delegate) {
    override fun getParentElementName(): String {
        return "comments"
    }

    override fun handle(element: StartElement) {
        val rawPost = CommentImpl(
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