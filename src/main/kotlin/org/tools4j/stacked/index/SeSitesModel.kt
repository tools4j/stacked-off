package org.tools4j.stacked.index

import org.apache.lucene.document.*

interface SeSite {
    val seSiteId: String
    val tinyName: String
    val name: String
    val longName: String?
    val url: String
    val imageUrl: String?
    val iconUrl: String?
    val databaseName: String?
    val tagline: String?
    val tagCss: String?
    val totalQuestions: String?
    val totalAnswers: String?
    val totalUsers: String?
    val totalComments: String?
    val totalTags: String?
    val lastPost: String?
    val oDataEndpoint: String?
    val badgeIconUrl: String?
    val urlDomain: String?

    fun addTo(doc: Document)
    fun fuzzyMatches(other: SeSite): Boolean
}

data class SeSiteImpl(
    override val seSiteId: String,
    override val tinyName: String,
    override val name: String,
    override val longName: String?,
    override val url: String,
    override val imageUrl: String?,
    override val iconUrl: String?,
    override val databaseName: String?,
    override val tagline: String?,
    override val tagCss: String?,
    override val totalQuestions: String?,
    override val totalAnswers: String?,
    override val totalUsers: String?,
    override val totalComments: String?,
    override val totalTags: String?,
    override val lastPost: String?,
    override val oDataEndpoint: String?,
    override val badgeIconUrl: String?) : SeSite {
    override val urlDomain: String = url.replace(Regex("http(s)?://"), "")

    constructor(doc: Document): this(
            doc.get("seSiteId"),
            doc.get("tinyName"),
            doc.get("name"),
            doc.get("longName"),
            doc.get("url"),
            doc.get("imageUrl"),
            doc.get("iconUrl"),
            doc.get("databaseName"),
            doc.get("tagline"),
            doc.get("tagCss"),
            doc.get("totalQuestions"),
            doc.get("totalAnswers"),
            doc.get("totalUsers"),
            doc.get("totalComments"),
            doc.get("totalTags"),
            doc.get("lastPost"),
            doc.get("oDataEndpoint"),
            doc.get("badgeIconUrl"))

    override fun addTo(doc: Document) {
        doc.add(StringField("seSiteId", seSiteId, Field.Store.YES))
        doc.add(TextField("tinyName", tinyName, Field.Store.YES))
        doc.add(TextField("name", name, Field.Store.YES))
        if (longName != null) doc.add(TextField("longName", longName, Field.Store.YES))
        doc.add(TextField("url", url, Field.Store.YES))
        if (imageUrl != null) doc.add(StoredField("imageUrl", imageUrl))
        if (iconUrl != null) doc.add(StoredField("iconUrl", iconUrl))
        if (databaseName != null) doc.add(StoredField("databaseName", databaseName))
        if (tagline != null) doc.add(StoredField("tagline", tagline))
        if (tagCss != null) doc.add(StoredField("tagCss", tagCss))
        if (totalQuestions != null) doc.add(StoredField("totalQuestions", totalQuestions))
        if (totalAnswers != null) doc.add(StoredField("totalAnswers", totalAnswers))
        if (totalUsers != null) doc.add(StoredField("totalUsers", totalUsers))
        if (totalComments != null) doc.add(StoredField("totalComments", totalComments))
        if (totalTags != null) doc.add(StoredField("totalTags", totalTags))
        if (lastPost != null) doc.add(StoredField("lastPost", lastPost))
        if (oDataEndpoint != null) doc.add(StoredField("oDataEndpoint", oDataEndpoint))
        if (badgeIconUrl != null) doc.add(StoredField("badgeIconUrl", badgeIconUrl))
    }

    override fun fuzzyMatches(other: SeSite): Boolean {
        var matches = 0
        matches += if(this.seSiteId == other.seSiteId) 1 else 0
        matches += if(this.tinyName == other.tinyName) 1 else 0
        matches += if(this.name == other.name) 1 else 0
        matches += if(this.longName == other.longName) 1 else 0
        matches += if(this.url == other.url) 1 else 0
        return matches >= 3
    }

    override fun toString(): String {
        return "SeSiteImpl(seSiteId='$seSiteId', tinyName=$tinyName, name=$name, url='$url')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SeSiteImpl

        if (seSiteId != other.seSiteId) return false
        if (tinyName != other.tinyName) return false
        if (name != other.name) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = seSiteId.hashCode()
        result = 31 * result + (tinyName.hashCode() ?: 0)
        result = 31 * result + (name.hashCode() ?: 0)
        result = 31 * result + url.hashCode()
        return result
    }
}
