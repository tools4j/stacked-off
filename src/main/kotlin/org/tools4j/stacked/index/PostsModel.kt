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

    private val MIN_RANK = 1.17549435E-38f

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
        if(body != null) doc.add(StoredField("htmlContent", body))
        if(lastActivityDate != null) doc.add(StoredField("lastActivityDate", lastActivityDate))
        if(tags != null) doc.add(StoredField("tags", tags))
        if(favoriteCount != null) doc.add(StoredField("favoriteCount", favoriteCount))
        if(title != null) doc.add(StoredField("title", title))
        if(parentId != null) doc.add(StringField("parentId", parentId, Field.Store.YES))
        doc.add(StringField("isQuestion", (parentId == null).toString() , Field.Store.NO))
        if(acceptedAnswerId != null) doc.add(StoredField("acceptedAnswerId", acceptedAnswerId))
        if(userId != null) doc.add(StoredField("userId", userId))
        return doc
    }

    fun convertToQuestionDocument(
        indexedSiteId: String,
        user: StagingUser?,
        answerCount: Int,
        aggregatedTextContent: String
    ): Document {
        val doc = Document()
        doc.add(StringField("uid", "p$indexedSiteId.$id", Field.Store.YES))
        doc.add(StoredField("postId", id))
        doc.add(StringField("type", "question", Field.Store.YES))
        doc.add(StringField("child", "N", Field.Store.YES))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        //https://stackoverflow.com/questions/42482451/lucene-6-recommended-way-to-store-numeric-fields-with-term-vocabulary
        doc.add(StoredField("answerCount", answerCount))
        doc.add(FeatureField("answerCount", "answerCountRank", calculateAnswerCountRank(answerCount)))
        if(title != null) doc.add(TextField("title", title, Field.Store.YES))
        if(acceptedAnswerId != null) doc.add(StoredField("acceptedAnswerUid", "p$indexedSiteId.$acceptedAnswerId"))
        if(tags != null) doc.add(StoredField("tags", tags))
        if(viewCount != null) doc.add(StoredField("viewCount", viewCount))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        if(score != null) doc.add(StoredField("score", score))
        doc.add(FeatureField("score", "scoreRank", calculateScoreRank()))
        if(body != null) doc.add(StoredField("htmlContent", body))
        doc.add(TextField("aggregatedTextContent", aggregatedTextContent, Field.Store.YES))
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
        doc.add(StoredField("postId", id))
        doc.add(StringField("type", "answer", Field.Store.YES))
        doc.add(StringField("child", "Y", Field.Store.NO))
        doc.add(StringField("indexedSiteId", indexedSiteId, Field.Store.YES))
        doc.add(StringField("parentUid", "p$indexedSiteId.$parentId", Field.Store.YES))
        if(creationDate != null) doc.add(StoredField("creationDate", creationDate))
        if(score != null) doc.add(StoredField("score", score))
        if(body != null) doc.add(StoredField("htmlContent", body))
        if(lastActivityDate != null) doc.add(StoredField("lastActivityDate", lastActivityDate))
        if(favoriteCount != null) doc.add(StoredField("favoriteCount", favoriteCount))
        if(userId != null) doc.add(StoredField("userUid", "u$indexedSiteId.$userId"))
        if(user?.reputation != null) doc.add(StoredField("userReputation", user.reputation))
        if(user?.displayName != null) doc.add(StoredField("userDisplayName", user.displayName))
        if(user?.accountId != null) doc.add(StoredField("userAccountId", user.accountId))
        return doc
    }

    private fun calculateAnswerCountRank(answerCount: Int): Float{
        return Math.max(MIN_RANK, answerCount.toFloat())
    }

    private fun calculateScoreRank(): Float{
        return if(score == null || score.toFloat() < MIN_RANK){
            MIN_RANK
        } else {
            return score.toFloat()
        }
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
    val postId: String
    val indexedSiteId: String
    val creationDate: String?
    val score: String?
    val htmlContent: String?
    val lastActivityDate: String?
    val favoriteCount: String?
    val comments: List<Comment>
}

data class Question(
    override val uid: String,
    override val postId: String,
    val title: String?,
    val acceptedAnswerUid: String?,
    val tags: String?,
    val viewCount: String?,
    override val creationDate: String?,
    override val score: String?,
    override val htmlContent: String?,
    val aggregatedTextContent: String?,
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
        doc.get("postId"),
        doc.get("title"),
        doc.get("acceptedAnswerUid"),
        doc.get("tags"),
        doc.get("viewCount"),
        doc.get("creationDate"),
        doc.get("score"),
        doc.get("htmlContent"),
        doc.get("aggregatedTextContent"),
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
}


data class Answer(override val uid: String,
                  override val postId: String,
                  override val indexedSiteId: String,
                  override val creationDate: String?,
                  override val score: String?,
                  override val htmlContent: String?,
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
        doc.get("postId"),
        doc.get("indexedSiteId"),
        doc.get("creationDate"),
        doc.get("score"),
        doc.get("htmlContent"),
        doc.get("lastActivityDate"),
        doc.get("favoriteCount"),
        doc.get("userUid"),
        doc.get("userReputation"),
        doc.get("userDisplayName"),
        doc.get("userAccountId"),
        doc.get("parentUid"),
        comments
    )
}

public fun stripHtmlTagsAndMultiWhitespace(html: String): String {
    return html
        .replace(Regex("\\<.*?>"), "")
        .replace(Regex("\\s\\s+"), " ")
        .trim()
}

