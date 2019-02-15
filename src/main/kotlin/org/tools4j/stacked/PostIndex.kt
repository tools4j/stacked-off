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

class PostIndex(val indexFactory: IndexFactory) {
    private val name = "posts"
    private lateinit var index: Directory
    private lateinit var analyzer: Analyzer
    private lateinit var queryParser: QueryParser

    fun init() {
        index = indexFactory.createIndex(name)

        analyzer = StandardAnalyzer()

        val fields: MutableMap<String, Float> = mutableMapOf(
            "title" to 10.0f,
            "body" to 7.0f,
            "tags" to 7.0f)

        queryParser = MultiFieldQueryParser(
            fields.keys.toTypedArray(),
            analyzer,
            fields
        );
    }

    fun addPosts(posts: List<RawPost>){
        val config = IndexWriterConfig(analyzer)
        val w = IndexWriter(index, config)
        for (post in posts) {
            addPost(w, post)
        }
        w.close()
    }

    fun query(queryString: String): List<RawPost> {
        val q = queryParser.parse(queryString);

        val hitsPerPage = 10
        val reader = DirectoryReader.open(index)
        val searcher = IndexSearcher(reader)
        val docs = searcher.search(q, hitsPerPage)
        val hits = docs.scoreDocs

        println("Found " + docs.totalHits + " hits.")
        return hits.map{searcher.doc(it.doc)}.map{createPostFromDocument(it)}.toList()
    }

    private fun createPostFromDocument(doc: Document):RawPost {
        return RawPostImpl(
            doc.get("id"),
            doc.get("postTypeId"),
            doc.get("creationDate"),
            doc.get("score"),
            doc.get("viewCount"),
            doc.get("body"),
            doc.get("ownerUserId"),
            doc.get("lastActivityDate"),
            doc.get("tags"),
            doc.get("parentId"),
            doc.get("favoriteCount"),
            doc.get("title"))
    }

    private fun addPost(w: IndexWriter, post: RawPost) {
        val doc = Document()
        doc.add(StringField("id", post.id!!, Field.Store.YES))
        if(post.postTypeId != null) doc.add(StoredField("postTypeId", post.postTypeId))
        if(post.creationDate != null) doc.add(StoredField("creationDate", post.creationDate))
        if(post.score != null) doc.add(StoredField("score", post.score))
        if(post.viewCount != null) doc.add(StoredField("viewCount", post.viewCount))
        if(post.body != null) doc.add(TextField("body", post.body, Field.Store.YES))
        if(post.ownerUserId != null) doc.add(StoredField("ownerUserId", post.ownerUserId))
        if(post.lastActivityDate != null) doc.add(StoredField("lastActivityDate", post.lastActivityDate))
        if(post.tags != null) doc.add(TextField("tags", post.tags, Field.Store.YES))
        if(post.parentId != null) doc.add(StoredField("parentId", post.parentId))
        if(post.favoriteCount != null) doc.add(StoredField("favoriteCount", post.favoriteCount))
        if(post.title != null) doc.add(TextField("title", post.title, Field.Store.YES))
        w.addDocument(doc)
    }
}