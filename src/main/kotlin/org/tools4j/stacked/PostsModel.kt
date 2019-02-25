package org.tools4j.stacked

import javax.xml.namespace.QName
import javax.xml.stream.events.StartElement


interface RawPost {
    val id: String
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
    val comments: List<Comment>
    fun containsComment(commentId: String): Boolean
}

interface Question: Post {
    val childPosts: List<Post>
    fun containsPost(postId: String): Boolean
}

data class PostImpl(val rawPost: RawPost, override val comments: List<Comment>): RawPost by rawPost, Post{
    override fun containsComment(commentId: String): Boolean{
        return comments.any { it.id == commentId }
    }
}

data class QuestionImpl(val post: Post, override val childPosts: List<Post>): Post by post, Question {
    constructor(rawPost: RawPost, comments: List<Comment>, childPosts: List<Post>)
            : this(PostImpl(rawPost, comments), childPosts)

    override fun containsPost(postId: String): Boolean{
        return post.id == postId || childPosts.any { it.id == postId }
    }

    override fun containsComment(commentId: String): Boolean {
        return post.containsComment(commentId)
                || childPosts.any { it.containsComment(commentId) }
    }
}

data class RawPostImpl(
    override val id: String,
    override val postTypeId: String?,
    override val creationDate: String?,
    override val score: String?,
    override val viewCount: String?,
    override val body:String?,
    override val ownerUserId: String?,
    override val lastActivityDate: String?,
    override val tags: String?,
    override val parentId: String?,
    override val favoriteCount: String?,
    override val title: String?) : RawPost


class PostXmlRowHandler(delegate: ItemHandler<RawPost>): XmlRowHandler<RawPost>(delegate) {
    override fun getParentElementName(): String {
        return "posts"
    }

    override fun handle(element: StartElement) {
        val rawPost = RawPostImpl(
            element.getAttributeByName(QName.valueOf("Id"))!!.value,
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

