package org.tools4j.stacked

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.Directory

class CommentIndex(indexFactory: IndexFactory)
    : AbstractIndex<Comment>(indexFactory, "comments") {

    override fun getIndexedFieldsAndRankings(): MutableMap<String, Float> {
        return mutableMapOf("text" to 10.0f)
    }

    override fun convertDocumentToItem(doc: Document): Comment = CommentImpl(
        doc.get("id"),
        doc.get("postId"),
        doc.get("score"),
        doc.get("text"),
        doc.get("creationDate"),
        doc.get("userId"))

    override fun convertItemToDocument(comment: Comment): Document {
        val doc = Document()
        doc.add(StringField("id", comment.id!!, Field.Store.YES))
        if(comment.postId != null) doc.add(StoredField("postId", comment.postId))
        if(comment.score != null) doc.add(StoredField("score", comment.score))
        if(comment.text != null) doc.add(TextField("text", comment.text, Field.Store.YES))
        if(comment.creationDate != null) doc.add(StoredField("creationDate", comment.creationDate))
        if(comment.userId != null) doc.add(StoredField("userId", comment.userId))
        return doc
    }
}