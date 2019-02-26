package org.tools4j.stacked.index

import org.apache.lucene.document.*
import org.apache.lucene.index.Term
import org.apache.lucene.search.TermQuery

class SiteIndex(indexFactory: IndexFactory)
    : AbstractIndex<Site>(indexFactory, "sites") {

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> {
        return mutableMapOf("tinyName" to 10.0f, "name" to 10.0f, "longName" to 7.0f, "url" to 10.0f)
    }

    override fun convertDocumentToItem(doc: Document): Site =
        SiteImpl(
            doc.get("id"),
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
            doc.get("badgeIconUrl")
        )

    override fun convertItemToDocument(site: Site): Document {
        val doc = Document()
        doc.add(StringField("id", site.id, Field.Store.YES))
        if(site.tinyName != null) doc.add(TextField("tinyName", site.tinyName, Field.Store.YES))
        if(site.name != null) doc.add(TextField("name", site.name, Field.Store.YES))
        if(site.longName != null) doc.add(TextField("longName", site.longName, Field.Store.YES))
        if(site.url != null) doc.add(TextField("url", site.url, Field.Store.YES))
        if(site.imageUrl != null) doc.add(StoredField("imageUrl", site.imageUrl))
        if(site.iconUrl != null) doc.add(StoredField("iconUrl", site.iconUrl))
        if(site.databaseName != null) doc.add(StoredField("databaseName", site.databaseName))
        if(site.tagline != null) doc.add(StoredField("tagline", site.tagline))
        if(site.tagCss != null) doc.add(StoredField("tagCss", site.tagCss))
        if(site.totalQuestions != null) doc.add(StoredField("totalQuestions", site.totalQuestions))
        if(site.totalAnswers != null) doc.add(StoredField("totalAnswers", site.totalAnswers))
        if(site.totalUsers != null) doc.add(StoredField("totalUsers", site.totalUsers))
        if(site.totalComments != null) doc.add(StoredField("totalComments", site.totalComments))
        if(site.totalTags != null) doc.add(StoredField("totalTags", site.totalTags))
        if(site.lastPost != null) doc.add(StoredField("lastPost", site.lastPost))
        if(site.oDataEndpoint != null) doc.add(StoredField("oDataEndpoint", site.oDataEndpoint))
        if(site.badgeIconUrl != null) doc.add(StoredField("badgeIconUrl", site.badgeIconUrl))
        return doc
    }
}