package org.tools4j.stacked

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StoredField
import org.apache.lucene.document.StringField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.TermQuery
import org.apache.lucene.store.Directory

class UserIndex(indexFactory: IndexFactory)
    : AbstractIndex<User>(indexFactory, "users") {

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> = HashMap()

    override fun convertDocumentToItem(doc: Document): User = UserImpl(
        doc.get("id"),
        doc.get("reputation"),
        doc.get("displayName"),
        doc.get("accountId"))

    override fun convertItemToDocument(user: User): Document {
        val doc = Document()
        doc.add(StringField("id", user.id, Field.Store.YES))
        if(user.reputation != null) doc.add(StoredField("reputation", user.reputation))
        if(user.displayName != null) doc.add(StoredField("displayName", user.displayName))
        if(user.accountId != null) doc.add(StoredField("accountId", user.accountId))
        return doc
    }
}