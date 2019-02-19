package org.tools4j.stacked

import java.util.function.Consumer
import javax.xml.namespace.QName
import javax.xml.stream.events.StartElement


interface RawPost {
    val id: String?
    val postTypeId: String?
    val creationDate: String?
    val score: String?
    val viewCount: String?
    val body: String?
    val ownerUserId: String?
    val lastActivityDate: String?
    val tags: String?
    val parentId: String?
    val favoriteCount: String?
    val title: String?
}

interface Post: RawPost {
    val commentModels: List<Comment>
}

interface ParentPost: Post {
    val childPosts: List<Post>
}

data class PostImpl(val rawPost: RawPost, override val commentModels: List<Comment>): RawPost by rawPost, Post
data class ParentPostImpl(val post: Post, override val childPosts: List<Post>): Post by post, ParentPost {
    constructor(rawPost: RawPost, commentModels: List<Comment>, childPosts: List<Post>)
            : this(PostImpl(rawPost, commentModels), childPosts)
}

data class RawPostImpl(
    override val id: String? = null,
    override val postTypeId: String? = null,
    override val creationDate: String? = null,
    override val score: String? = null,
    override val viewCount: String? = null,
    override val body:String? = null,
    override val ownerUserId: String? = null,
    override val lastActivityDate: String? = null,
    override val tags: String? = null,
    override val parentId: String? = null,
    override val favoriteCount: String? = null,
    override val title: String? = null) : RawPost


class PostXmlRowHandler(delegate: ItemHandler<RawPost>): XmlRowHandler<RawPost>(delegate) {
    override fun getParentElementName(): String {
        return "posts"
    }

    override fun handle(element: StartElement) {
        val rawPost = RawPostImpl(
            element.getAttributeByName(QName.valueOf("Id"))?.value,
            element.getAttributeByName(QName.valueOf("PostTypeId"))?.value,
            element.getAttributeByName(QName.valueOf("CreationDate"))?.value,
            element.getAttributeByName(QName.valueOf("Score"))?.value,
            element.getAttributeByName(QName.valueOf("ViewCount"))?.value,
            element.getAttributeByName(QName.valueOf("Body"))?.value,
            element.getAttributeByName(QName.valueOf("OwnerUserId"))?.value,
            element.getAttributeByName(QName.valueOf("LastActivityDate"))?.value,
            element.getAttributeByName(QName.valueOf("Tags"))?.value,
            element.getAttributeByName(QName.valueOf("ParentId"))?.value,
            element.getAttributeByName(QName.valueOf("FavoriteCount"))?.value,
            element.getAttributeByName(QName.valueOf("Title"))?.value
        )
        delegate.handle(rawPost)
    }
}

