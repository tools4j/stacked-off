package org.tools4j.stacked.index

import org.apache.lucene.document.Document
import org.apache.lucene.index.Term
import org.apache.lucene.search.TermQuery

class CommentIndex(indexFactory: IndexFactory)
    : AbstractIndex<RawComment>(indexFactory, "comments") {

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> {
        return mutableMapOf("text" to 10.0f)
    }

    override fun convertDocumentToItem(doc: Document): RawComment = RawCommentImpl(doc)

    override fun convertItemToDocument(rawComment: RawComment): Document = rawComment.convertToDocument()

    fun getByPostUid(postUid: String): List<RawComment> {
        return search{it.search(TermQuery(Term("postUid", postUid)), 100)}
    }
}