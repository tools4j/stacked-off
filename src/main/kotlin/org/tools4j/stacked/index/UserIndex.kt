package org.tools4j.stacked.index

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StoredField
import org.apache.lucene.document.StringField

class UserIndex(indexFactory: IndexFactory)
    : AbstractIndex<User>(indexFactory, "users") {

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> = HashMap()

    override fun convertDocumentToItem(doc: Document): User = UserImpl(doc)

    override fun convertItemToDocument(user: User): Document = user.convertToDocument()
}