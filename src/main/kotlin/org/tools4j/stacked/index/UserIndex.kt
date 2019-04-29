package org.tools4j.stacked.index

import org.apache.lucene.document.Document

class UserIndex(indexFactory: IndexFactory)
    : SingleTypedIndex<User>(indexFactory, "users") {

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> = HashMap()

    override fun convertDocumentToItem(doc: Document): User = UserImpl(doc)

    override fun convertItemToDocument(user: User): Document = user.convertToDocument()
}