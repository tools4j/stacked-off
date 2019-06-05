package org.tools4j.stacked.index

import org.apache.lucene.document.Document


class StagingPostIndex(indexFactory: IndexFactory)
    : TypedIndex<StagingPost>(indexFactory,"posts") {

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> = HashMap()

    override fun convertDocumentToItem(doc: Document): StagingPost = StagingPost(doc)

    override fun convertItemToDocument(post: StagingPost): Document = post.convertToDocument()

    fun getByParentId(parentId: String): List<StagingPost> {
        return searchByTerm("parentId", parentId, UnscoredCollector())
    }
}