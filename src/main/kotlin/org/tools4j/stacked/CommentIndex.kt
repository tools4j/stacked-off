package org.tools4j.stacked

import org.apache.lucene.document.*
import org.apache.lucene.index.Term
import org.apache.lucene.search.TermQuery

class CommentIndex(indexFactory: IndexFactory)
    : AbstractIndex<RawComment>(indexFactory, "comments") {

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> {
        return mutableMapOf("text" to 10.0f)
    }

    override fun convertDocumentToItem(doc: Document): RawComment = RawCommentImpl(
        doc.get("id"),
        doc.get("postId"),
        doc.get("score"),
        doc.get("text"),
        doc.get("creationDate"),
        doc.get("userId"))

    override fun convertItemToDocument(rawComment: RawComment): Document {
        val doc = Document()
        doc.add(StringField("id", rawComment.id!!, Field.Store.YES))
        if(rawComment.postId != null) doc.add(StringField("postId", rawComment.postId, Field.Store.YES))
        if(rawComment.score != null) doc.add(StoredField("score", rawComment.score))
        if(rawComment.text != null) doc.add(TextField("text", rawComment.text, Field.Store.YES))
        if(rawComment.creationDate != null) doc.add(StoredField("creationDate", rawComment.creationDate))
        if(rawComment.userId != null) doc.add(StoredField("userId", rawComment.userId))
        return doc
    }

    fun getByPostId(postId: String): List<RawComment> {
        return search{it.search(TermQuery(Term("postId", postId)), 100)}
    }
}