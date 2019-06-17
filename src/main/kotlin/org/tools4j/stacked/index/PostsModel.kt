package org.tools4j.stacked.index

import org.apache.lucene.document.*
import org.apache.lucene.search.Explanation
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
        doc.get("htmlContent"),
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
        if(body != null) doc.add(TextField("htmlContent", body, Field.Store.YES))
        if(lastActivityDate != null) doc.add(StoredField("lastActivityDate", lastActivityDate))
        if(tags != null) doc.add(TextField("tags", tags, Field.Store.YES))
        if(favoriteCount != null) doc.add(StoredField("favoriteCount", favoriteCount))
        if(title != null) doc.add(TextField("title", title, Field.Store.YES))
        if(parentId != null) doc.add(StringField("parentId", parentId, Field.Store.YES))
        if(acceptedAnswerId != null) doc.add(TextField("acceptedAnswerId", acceptedAnswerId, Field.Store.YES))
        if(userId != null) doc.add(StoredField("userId", userId))
        return doc
    }

    fun convertToQuestionDocument(
        indexedSiteId: String,
        user: StagingUser?,
        answerCount: Int
    ): Document {
        val doc = Document()
        doc.add(StringField("uid", "p$indexedSiteId.$id", Field.Store.YES))
        doc.add(StringField("type", "question", Field.Store.YES))
        doc.add(StringField("child", "N", Field.Store.YES))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        doc.add(StoredField("answerCount", Integer.valueOf(answerCount)))
        if(title != null) doc.add(TextField("title", title, Field.Store.YES))
        if(acceptedAnswerId != null) doc.add(TextField("acceptedAnswerUid", "p$indexedSiteId.$acceptedAnswerId", Field.Store.YES))
        if(tags != null) doc.add(TextField("tags", tags, Field.Store.YES))
        if(viewCount != null) doc.add(StoredField("viewCount", viewCount))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        if(score != null) doc.add(StoredField("score", score))
        if(body != null) doc.add(StoredField("htmlContent", body))
        if(body != null) doc.add(TextField("textContent", stripHtmlTagsAndMultiWhitespace(body), Field.Store.YES))
        if(lastActivityDate != null) doc.add(StoredField("lastActivityDate", lastActivityDate))
        if(favoriteCount != null) doc.add(StoredField("favoriteCount", favoriteCount))
        if(userId != null) doc.add(StoredField("userUid", "u$indexedSiteId.$userId"))
        if(user?.reputation != null) doc.add(StoredField("userReputation", user.reputation))
        if(user?.displayName != null) doc.add(StoredField("userDisplayName", user.displayName))
        if(user?.accountId != null) doc.add(StoredField("userAccountId", user.accountId))
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
        if(body != null) doc.add(StoredField("htmlContent", body))
        if(body != null) doc.add(TextField("textContent", stripHtmlTagsAndMultiWhitespace(body), Field.Store.YES))
        if(lastActivityDate != null) doc.add(StoredField("lastActivityDate", lastActivityDate))
        if(favoriteCount != null) doc.add(StoredField("favoriteCount", favoriteCount))
        if(userId != null) doc.add(StoredField("userUid", "u$indexedSiteId.$userId"))
        if(user?.reputation != null) doc.add(StoredField("userReputation", user.reputation))
        if(user?.displayName != null) doc.add(StoredField("userDisplayName", user.displayName))
        if(user?.accountId != null) doc.add(StoredField("userAccountId", user.accountId))
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
    val htmlContent: String?
    val textContent: String?
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
    override val htmlContent: String?,
    override val textContent: String?,
    override val lastActivityDate: String?,
    override val favoriteCount: String?,
    override val userUid: String?,
    override val userReputation: String?,
    override val userDisplayName: String?,
    override val userAccountId: String?,
    val indexedSite: IndexedSite,
    override val comments: List<Comment>,
    val answers: List<Answer>,
    val explanation: Explanation?
): Post {

    constructor(doc: Document,
                indexedSite: IndexedSite,
                comments: List<Comment>,
                answers: List<Answer>,
                explanation: Explanation?): this(

        doc.get("uid"),
        doc.get("title"),
        doc.get("acceptedAnswerUid"),
        doc.get("tags"),
        doc.get("viewCount"),
        doc.get("creationDate"),
        doc.get("score"),
        doc.get("htmlContent"),
        doc.get("textContent"),
        doc.get("lastActivityDate"),
        doc.get("favoriteCount"),
        doc.get("userUid"),
        doc.get("userReputation"),
        doc.get("userDisplayName"),
        doc.get("userAccountId"),
        indexedSite,
        comments,
        answers,
        explanation
    )

    override val indexedSiteId: String
        get() = indexedSite.indexedSiteId

    override fun convertToDocument(): Document {
        val doc = Document()
        doc.add(StringField("uid", uid, Field.Store.YES))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        if(score != null) doc.add(StoredField("score", score))
        if(htmlContent != null) doc.add(StoredField("htmlContent", htmlContent))
        if(textContent != null) doc.add(TextField("textContent", textContent, Field.Store.YES))
        if(lastActivityDate != null) doc.add(StoredField("lastActivityDate", lastActivityDate))
        if(favoriteCount != null) doc.add(StoredField("favoriteCount", favoriteCount))
        if(userUid != null) doc.add(StoredField("userUid", userUid))
        if(userReputation != null) doc.add(StoredField("userReputation", userReputation))
        if(userDisplayName != null) doc.add(StoredField("userDisplayName", userDisplayName))
        if(userAccountId != null) doc.add(StoredField("userAccountId", userAccountId))
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
        sb.append(comments.map{"    " + it.uid + ":" + it.textContent  }.joinToString("\n")).append("\n")
        sb.append("----------------------------------------------------------\n")
        for (childPost in answers) {
            sb.append("    ").append(childPost.uid).append(":").append(childPost.htmlContent!!.substring(0, 10)).append("\n")
            sb.append(childPost.comments.map{"        " + it.uid + ":" + it.textContent  }.joinToString("\n")).append("\n")
        }
        return sb.toString()
    }

}


data class Answer(override val uid: String,
                  override val indexedSiteId: String,
                  override val creationDate: String?,
                  override val score: String?,
                  override val htmlContent: String?,
                  override val textContent: String?,
                  override val lastActivityDate: String?,
                  override val favoriteCount: String?,
                  override val userUid: String?,
                  override val userReputation: String?,
                  override val userDisplayName: String?,
                  override val userAccountId: String?,
                  val parentUid: String,
                  override val comments: List<Comment>
): Post {

    constructor(doc: Document,
                comments: List<Comment>): this(

        doc.get("uid"),
        doc.get("indexedSiteId"),
        doc.get("creationDate"),
        doc.get("score"),
        doc.get("htmlContent"),
        doc.get("textContent"),
        doc.get("lastActivityDate"),
        doc.get("favoriteCount"),
        doc.get("userUid"),
        doc.get("userReputation"),
        doc.get("userDisplayName"),
        doc.get("userAccountId"),
        doc.get("parentUid"),
        comments
    )

    override fun convertToDocument(): Document {
        val doc = Document()
        doc.add(StringField("uid", uid, Field.Store.YES))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        if(score != null) doc.add(StoredField("score", score))
        if(htmlContent != null) doc.add(StoredField("htmlContent", htmlContent))
        if(textContent != null) doc.add(TextField("textContent", textContent, Field.Store.YES))
        if(lastActivityDate != null) doc.add(StoredField("lastActivityDate", lastActivityDate))
        if(favoriteCount != null) doc.add(StoredField("favoriteCount", favoriteCount))
        if(userUid != null) doc.add(StoredField("userUid", userUid))
        if(userReputation != null) doc.add(StoredField("userReputation", userReputation))
        if(userDisplayName != null) doc.add(StoredField("userDisplayName", userDisplayName))
        if(userAccountId != null) doc.add(StoredField("userAccountId", userAccountId))
        doc.add(StringField("parentUid", parentUid, Field.Store.YES))
        return doc
    }
}

public fun stripHtmlTagsAndMultiWhitespace(html: String): String {
    return html
        .replace(Regex("\\<.*?>"), "")
        .replace(Regex("\\s\\s+"), " ")
        .trim()
}

