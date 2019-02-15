package org.tools4j.stacked

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

interface Comment {
    val id: String?
    val postId: String?
    val score: String?
    val text: String?
    val creationDate: String?
    val userId: String?
}

@JsonRootName("comments")
data class Comments(
    @set:JsonProperty("row")
    var comments: List<CommentImpl>? = null){

    companion object {
        @JvmStatic
        fun fromXmlOnClasspath(onClasspath: String): Comments {
            return XmlDoc.parseAs(onClasspath)
        }
    }
}

@JsonRootName("row")
data class CommentImpl(
    @set:JsonProperty("Id")
    override var id: String?,

    @set:JsonProperty("PostId")
    override var postId: String?,

    @set:JsonProperty("Score")
    override var score: String?,

    @set:JsonProperty("Text")
    override var text: String?,

    @set:JsonProperty("CreationDate")
    override var creationDate: String?,

    @set:JsonProperty("UserId")
    override var userId: String?) : Comment