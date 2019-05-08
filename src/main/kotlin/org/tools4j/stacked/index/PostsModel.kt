package org.tools4j.stacked.index

import org.apache.lucene.document.*
import javax.xml.namespace.QName
import javax.xml.stream.events.StartElement

class StagingPost(
    val id: String,
    val creationDate: String?,
    val score: String?,
    val viewCount: String?,
    val body: String?,
    val lastActivityDate: String?,
    val tags: String?,
    val favoriteCount: String?,
    val title: String?,
    val parentId: String?,
    val acceptedAnswerId: String?,
    val userId: String?){

    constructor(doc: Document): this(
        doc.get("id"),
        doc.get("creationDate"),
        doc.get("score"),
        doc.get("viewCount"),
        doc.get("body"),
        doc.get("lastActivityDate"),
        doc.get("tags"),
        doc.get("favoriteCount"),
        doc.get("title"),
        doc.get("parentId"),
        doc.get("acceptedAnswerId"),
        doc.get("userId")
    )

    fun convertToDocument(): Document {
        val doc = Document()
        doc.add(StringField("id", id, Field.Store.YES))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        if(score != null) doc.add(StoredField("score", score))
        if(viewCount != null) doc.add(StoredField("viewCount", viewCount))
        if(body != null) doc.add(TextField("body", body, Field.Store.YES))
        if(lastActivityDate != null) doc.add(StoredField("lastActivityDate", lastActivityDate))
        if(tags != null) doc.add(TextField("tags", tags, Field.Store.YES))
        if(favoriteCount != null) doc.add(StoredField("favoriteCount", favoriteCount))
        if(title != null) doc.add(TextField("title", title, Field.Store.YES))
        if(parentId != null) doc.add(StringField("parentId", parentId, Field.Store.YES))
        if(acceptedAnswerId != null) doc.add(TextField("acceptedAnswerId", acceptedAnswerId, Field.Store.YES))
        if(userId != null) doc.add(StoredField("userId", userId))
        return doc
    }

    fun convertToQuestionDocument(indexedSiteId: String, user: StagingUser?): Document {
        val doc = Document()
        doc.add(StringField("uid", "p$indexedSiteId.$id", Field.Store.YES))
        doc.add(StringField("type", "question", Field.Store.YES))
        doc.add(StringField("child", "N", Field.Store.YES))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        if(title != null) doc.add(TextField("title", title, Field.Store.YES))
        if(acceptedAnswerId != null) doc.add(TextField("acceptedAnswerUid", "p$indexedSiteId.$acceptedAnswerId", Field.Store.YES))
        if(tags != null) doc.add(TextField("tags", tags, Field.Store.YES))
        if(viewCount != null) doc.add(StoredField("viewCount", viewCount))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        if(score != null) doc.add(StoredField("score", score))
        if(body != null) doc.add(TextField("body", body, Field.Store.YES))
        if(lastActivityDate != null) doc.add(StoredField("lastActivityDate", lastActivityDate))
        if(favoriteCount != null) doc.add(StoredField("favoriteCount", favoriteCount))
        if(userId != null) doc.add(StoredField("userUid", "u$indexedSiteId.$userId"))
        if(user?.reputation != null) doc.add(StoredField("userReputation", user.reputation))
        if(user?.displayName != null) doc.add(StoredField("userDisplayName", user.displayName))
        return doc
    }

    fun convertToAnswerDocument(indexedSiteId: String, user: StagingUser?): Document {
        val doc = Document()
        doc.add(StringField("uid", "p$indexedSiteId.$id", Field.Store.YES))
        doc.add(StringField("type", "answer", Field.Store.YES))
        doc.add(StringField("child", "Y", Field.Store.NO))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        doc.add(StringField("parentUid", "p$indexedSiteId.$parentId", Field.Store.YES))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        if(score != null) doc.add(StoredField("score", score))
        if(body != null) doc.add(TextField("body", body, Field.Store.YES))
        if(lastActivityDate != null) doc.add(StoredField("lastActivityDate", lastActivityDate))
        if(favoriteCount != null) doc.add(StoredField("favoriteCount", favoriteCount))
        if(userId != null) doc.add(StoredField("userUid", "u$indexedSiteId.$userId"))
        if(user?.reputation != null) doc.add(StoredField("userReputation", user.reputation))
        if(user?.displayName != null) doc.add(StoredField("userDisplayName", user.displayName))
        return doc
    }
}

class PostXmlRowHandler(delegate: ItemHandler<StagingPost>): XmlRowHandler<StagingPost>(delegate) {
    override fun handle(element: StartElement) {
        val xmlPost = StagingPost(
            element.getAttributeByName(QName.valueOf("Id"))!!.value,
            element.getAttributeByName(QName.valueOf("CreationDate"))?.value,
            element.getAttributeByName(QName.valueOf("Score"))?.value,
            element.getAttributeByName(QName.valueOf("ViewCount"))?.value,
            element.getAttributeByName(QName.valueOf("Body"))?.value,
            element.getAttributeByName(QName.valueOf("LastActivityDate"))?.value,
            element.getAttributeByName(QName.valueOf("Tags"))?.value,
            element.getAttributeByName(QName.valueOf("FavoriteCount"))?.value,
            element.getAttributeByName(QName.valueOf("Title"))?.value,
            element.getAttributeByName(QName.valueOf("ParentId"))?.value,
            element.getAttributeByName(QName.valueOf("AcceptedAnswerId"))?.value,
            element.getAttributeByName(QName.valueOf("OwnerUserId"))?.value
            )
        delegate.handle(xmlPost)
    }
}

interface Post: ContainsPrimaryUserFields {
    val uid: String
    val indexedSiteId: String
    val creationDate: String?
    val score: String?
    val body: String?
    val lastActivityDate: String?
    val favoriteCount: String?
    val comments: List<Comment>

    fun convertToDocument(): Document

}

data class Question(
    override val uid: String,
    val title: String?,
    val acceptedAnswerUid: String?,
    val tags: String?,
    val viewCount: String?,
    override val creationDate: String?,
    override val score: String?,
    override val body: String?,
    override val lastActivityDate: String?,
    override val favoriteCount: String?,
    override val userUid: String?,
    override val userReputation: String?,
    override val userDisplayName: String?,
    val indexedSite: IndexedSite,
    override val comments: List<Comment>,
    val answers: List<Answer>
): Post {

    constructor(doc: Document,
                indexedSite: IndexedSite,
                comments: List<Comment>,
                answers: List<Answer>): this(

        doc.get("uid"),
        doc.get("title"),
        doc.get("acceptedAnswerUid"),
        doc.get("tags"),
        doc.get("viewCount"),
        doc.get("creationDate"),
        doc.get("score"),
        doc.get("body"),
        doc.get("lastActivityDate"),
        doc.get("favoriteCount"),
        doc.get("userUid"),
        doc.get("userReputation"),
        doc.get("userDisplayName"),
        indexedSite,
        comments,
        answers
    )

    override val indexedSiteId: String
        get() = indexedSite.indexedSiteId

    override fun convertToDocument(): Document {
        val doc = Document()
        doc.add(StringField("uid", uid, Field.Store.YES))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        if(score != null) doc.add(StoredField("score", score))
        if(body != null) doc.add(TextField("body", body, Field.Store.YES))
        if(lastActivityDate != null) doc.add(StoredField("lastActivityDate", lastActivityDate))
        if(favoriteCount != null) doc.add(StoredField("favoriteCount", favoriteCount))
        if(userUid != null) doc.add(StoredField("userUid", userUid))
        if(userReputation != null) doc.add(StoredField("userReputation", userReputation))
        if(userDisplayName != null) doc.add(StoredField("userDisplayName", userDisplayName))
        if(viewCount != null) doc.add(StoredField("viewCount", viewCount))
        if(tags != null) doc.add(TextField("tags", tags, Field.Store.YES))
        if(title != null) doc.add(TextField("title", title, Field.Store.YES))
        if(acceptedAnswerUid != null) doc.add(TextField("acceptedAnswerUid", acceptedAnswerUid, Field.Store.YES))
        return doc
    }

    fun toPrettyString(): String {
        val sb = StringBuilder()
        sb.append("----------------------------------------------------------\n")
        sb.append(indexedSite.seSite.urlDomain).append(":").append(uid).append(":").append(title).append("\n")
        sb.append(comments.map{"    " + it.uid + ":" + it.text  }.joinToString("\n")).append("\n")
        sb.append("----------------------------------------------------------\n")
        for (childPost in answers) {
            sb.append("    ").append(childPost.uid).append(":").append(childPost.body!!.substring(0, 10)).append("\n")
            sb.append(childPost.comments.map{"        " + it.uid + ":" + it.text  }.joinToString("\n")).append("\n")
        }
        return sb.toString()
    }

}


data class Answer(override val uid: String,
                  override val indexedSiteId: String,
                  override val creationDate: String?,
                  override val score: String?,
                  override val body: String?,
                  override val lastActivityDate: String?,
                  override val favoriteCount: String?,
                  override val userUid: String?,
                  override val userReputation: String?,
                  override val userDisplayName: String?,
                  val parentUid: String,
                  override val comments: List<Comment>
): Post {

    constructor(doc: Document,
                comments: List<Comment>): this(

        doc.get("uid"),
        doc.get("indexedSiteId"),
        doc.get("creationDate"),
        doc.get("score"),
        doc.get("body"),
        doc.get("lastActivityDate"),
        doc.get("favoriteCount"),
        doc.get("userUid"),
        doc.get("userReputation"),
        doc.get("userDisplayName"),
        doc.get("parentUid"),
        comments
    )

    override fun convertToDocument(): Document {
        val doc = Document()
        doc.add(StringField("uid", uid, Field.Store.YES))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        if(score != null) doc.add(StoredField("score", score))
        if(body != null) doc.add(TextField("body", body, Field.Store.YES))
        if(lastActivityDate != null) doc.add(StoredField("lastActivityDate", lastActivityDate))
        if(favoriteCount != null) doc.add(StoredField("favoriteCount", favoriteCount))
        if(userUid != null) doc.add(StoredField("userUid", userUid))
        if(userReputation != null) doc.add(StoredField("userReputation", userReputation))
        if(userDisplayName != null) doc.add(StoredField("userDisplayName", userDisplayName))
        doc.add(StringField("parentUid", parentUid, Field.Store.YES))
        return doc
    }
}

