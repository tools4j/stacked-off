package org.tools4j.stacked.index

import org.apache.lucene.document.*
import javax.xml.namespace.QName
import javax.xml.stream.events.StartElement


interface RawPost {
    val uid: String
    val indexedSiteId: String
    val postTypeId: String?
    val creationDate: String?
    val score: String?
    val viewCount: String?
    val body: String?
    val ownerUserUid: String?
    val lastActivityDate: String?
    val tags: String?
    val favoriteCount: String?
    val title: String?
    val parentUid: String?
    fun convertToDocument(): Document
}

interface Post: RawPost {
    val ownerUser: User?
    val comments: List<Comment>
    fun containsComment(commentUid: String): Boolean
}

interface Question: Post {
    val childPosts: List<Post>
    fun containsPost(postId: String): Boolean
}

data class PostImpl(val rawPost: RawPost, override val ownerUser: User?, override val comments: List<Comment>): RawPost by rawPost,
    Post {
    override fun containsComment(commentUid: String): Boolean{
        return comments.any { it.uid == commentUid }
    }
}

data class QuestionImpl(val post: Post, override val childPosts: List<Post>): Post by post,
    Question {

    override fun containsPost(postUid: String): Boolean{
        return post.uid == postUid || childPosts.any { it.uid == postUid }
    }

    override fun containsComment(commentUid: String): Boolean {
        return post.containsComment(commentUid)
                || childPosts.any { it.containsComment(commentUid) }
    }
}

data class RawPostImpl(
    private val id: String,
    override val indexedSiteId: String,
    override val postTypeId: String?,
    override val creationDate: String?,
    override val score: String?,
    override val viewCount: String?,
    override val body:String?,
    private val ownerUserId: String?,
    override val lastActivityDate: String?,
    override val tags: String?,
    private val parentId: String?,
    override val favoriteCount: String?,
    override val title: String?) : RawPost{
    
    constructor(doc: Document): this(
        doc.get("id"),
        doc.get("indexedSiteId"),
        doc.get("postTypeId"),
        doc.get("creationDate"),
        doc.get("score"),
        doc.get("viewCount"),
        doc.get("body"),
        doc.get("ownerUserId"),
        doc.get("lastActivityDate"),
        doc.get("tags"),
        doc.get("parentId"),
        doc.get("favoriteCount"),
        doc.get("title")
    )
    
    override val ownerUserUid: String?
        get() = if(ownerUserId == null) null else "$indexedSiteId.$ownerUserId"

    override val parentUid: String?
        get() = if(parentId == null) null else "$indexedSiteId.$parentId"

    override val uid: String
        get() = "$indexedSiteId.$id"

    override fun convertToDocument(): Document {
        val doc = Document()
        doc.add(StoredField("id", id))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        doc.add(StringField("uid", uid, Field.Store.NO))
        if(postTypeId != null) doc.add(StoredField("postTypeId", postTypeId))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        if(score != null) doc.add(StoredField("score", score))
        if(viewCount != null) doc.add(StoredField("viewCount", viewCount))
        if(body != null) doc.add(TextField("body", body, Field.Store.YES))
        if(ownerUserId != null) doc.add(StoredField("ownerUserId", ownerUserId))
        if(lastActivityDate != null) doc.add(StoredField("lastActivityDate", lastActivityDate))
        if(tags != null) doc.add(TextField("tags", tags, Field.Store.YES))
        if(parentId != null) doc.add(StoredField("parentId", parentId))
        if(parentUid != null) doc.add(StringField("parentUid", parentUid, Field.Store.NO))
        if(favoriteCount != null) doc.add(StoredField("favoriteCount", favoriteCount))
        if(title != null) doc.add(TextField("title", title, Field.Store.YES))
        return doc
    }
}


class PostXmlRowHandler(delegateProvider: () -> ItemHandler<RawPost>): XmlRowHandler<RawPost>(delegateProvider) {
    override fun handle(element: StartElement, indexedSiteId: String) {
        val rawPost = RawPostImpl(
            element.getAttributeByName(QName.valueOf("Id"))!!.value,
            indexedSiteId,
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

