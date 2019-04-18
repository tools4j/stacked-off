package org.tools4j.stacked.index

import org.apache.lucene.document.*
import org.apache.lucene.index.Term
import org.apache.lucene.search.TermQuery

class PostIndex(indexFactory: IndexFactory)
    : AbstractIndex<RawPost>(indexFactory,"posts") {

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> = mutableMapOf(
        "title" to 10.0f,
        "body" to 7.0f,
        "tags" to 7.0f)

    override fun convertDocumentToItem(doc: Document): RawPost = RawPostImpl(doc)

    override fun convertItemToDocument(post: RawPost): Document = post.convertToDocument()

    fun getByParentUid(parentUid: String): List<RawPost> {
        return search{it.search(TermQuery(Term("parentUid", parentUid)), 10000)}
    }
}