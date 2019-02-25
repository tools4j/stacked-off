package org.tools4j.stacked

import org.apache.lucene.document.*
import org.apache.lucene.index.Term
import org.apache.lucene.search.TermQuery

class PostIndex(indexFactory: IndexFactory)
    : AbstractIndex<RawPost>(indexFactory,"posts") {

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> = mutableMapOf(
        "title" to 10.0f,
        "body" to 7.0f,
        "tags" to 7.0f)

    override fun convertDocumentToItem(doc: Document): RawPost {
        return RawPostImpl(
            doc.get("id"),
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
            doc.get("title"))    }

    override fun convertItemToDocument(post: RawPost): Document {
        val doc = Document()
        doc.add(StringField("id", post.id, Field.Store.YES))
        if(post.postTypeId != null) doc.add(StoredField("postTypeId", post.postTypeId))
        if(post.creationDate != null) doc.add(StoredField("creationDate", post.creationDate))
        if(post.score != null) doc.add(StoredField("score", post.score))
        if(post.viewCount != null) doc.add(StoredField("viewCount", post.viewCount))
        if(post.body != null) doc.add(TextField("body", post.body, Field.Store.YES))
        if(post.ownerUserId != null) doc.add(StoredField("ownerUserId", post.ownerUserId))
        if(post.lastActivityDate != null) doc.add(StoredField("lastActivityDate", post.lastActivityDate))
        if(post.tags != null) doc.add(TextField("tags", post.tags, Field.Store.YES))
        if(post.parentId != null) doc.add(TextField("parentId", post.parentId, Field.Store.YES))
        if(post.favoriteCount != null) doc.add(StoredField("favoriteCount", post.favoriteCount))
        if(post.title != null) doc.add(TextField("title", post.title, Field.Store.YES))
        return doc
    }

    fun getByParentPostId(parentPostId: String): List<RawPost> {
        return search{it.search(TermQuery(Term("parentId", parentPostId)), 1)}
    }
}