package org.tools4j.stacked.index

import org.apache.lucene.document.Document

class StagingUserIndex(indexFactory: IndexFactory)
    : TypedIndex<StagingUser>(indexFactory, "users") {

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> = HashMap()

    override fun convertDocumentToItem(doc: Document): StagingUser = StagingUser(doc)

    override fun convertItemToDocument(user: StagingUser): Document = user.convertToDocument()
}