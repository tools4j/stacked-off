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

class CommentIndex(val indexFactory: IndexFactory) {
    private val name = "comments"
    private lateinit var index: Directory
    private lateinit var analyzer: Analyzer
    private lateinit var queryParser: QueryParser

    fun init() {
        index = indexFactory.createIndex(name)

        analyzer = StandardAnalyzer()

        val fields: MutableMap<String, Float> = mutableMapOf("text" to 10.0f)

        queryParser = MultiFieldQueryParser(
            fields.keys.toTypedArray(),
            analyzer,
            fields
        );
    }

    fun addComments(comments: List<Comment>){
        val config = IndexWriterConfig(analyzer)
        val w = IndexWriter(index, config)
        for (comment in comments) {
            addComment(w, comment)
        }
        w.close()
    }

    fun query(queryString: String): List<Comment> {
        val q = queryParser.parse(queryString);

        val hitsPerPage = 10
        val reader = DirectoryReader.open(index)
        val searcher = IndexSearcher(reader)
        val docs = searcher.search(q, hitsPerPage)
        val hits = docs.scoreDocs

        println("Found " + docs.totalHits + " hits.")
        return hits.map{searcher.doc(it.doc)}.map{createCommentFromDocument(it)}.toList()
    }

    private fun createCommentFromDocument(doc: Document):Comment {
        return CommentImpl(
            doc.get("id"),
            doc.get("postId"),
            doc.get("score"),
            doc.get("text"),
            doc.get("creationDate"),
            doc.get("userId"))
    }

    private fun addComment(w: IndexWriter, comment: Comment) {
        val doc = Document()
        doc.add(StringField("id", comment.id!!, Field.Store.YES))
        if(comment.postId != null) doc.add(StoredField("postId", comment.postId))
        if(comment.score != null) doc.add(StoredField("score", comment.score))
        if(comment.text != null) doc.add(TextField("text", comment.text, Field.Store.YES))
        if(comment.creationDate != null) doc.add(StoredField("creationDate", comment.creationDate))
        if(comment.userId != null) doc.add(StoredField("userId", comment.userId))
        w.addDocument(doc)
    }
}