package org.tools4j.stacked

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName


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

@JsonRootName("posts")
data class Posts(
    @set:JsonProperty("row")
    var posts: List<RawPostImpl>? = null){

    companion object {
        @JvmStatic
        fun fromXmlOnClasspath(onClasspath: String): Posts{
            return XmlDoc.parseAs(onClasspath)
        }
    }
}

@JsonRootName("row")
data class RawPostImpl(
    @set:JsonProperty("Id")
    override var id: String? = null,

    @set:JsonProperty("PostTypeId")
    override var postTypeId: String? = null,

    @set:JsonProperty("CreationDate")
    override var creationDate: String? = null,

    @set:JsonProperty("Score")
    override var score: String? = null,

    @set:JsonProperty("ViewCount")
    override var viewCount: String? = null,

    @set:JsonProperty("Body")
    override var body:String? = null,

    @set:JsonProperty("OwnerUserId")
    override var ownerUserId: String? = null,

    @set:JsonProperty("LastActivityDate")
    override var lastActivityDate: String? = null,

    @set:JsonProperty("Tags")
    override var tags: String? = null,

    @set:JsonProperty("ParentId")
    override var parentId: String? = null,

    @set:JsonProperty("FavoriteCount")
    override var favoriteCount: String? = null,

    @set:JsonProperty("Title")
    override var title: String? = null) : RawPost

