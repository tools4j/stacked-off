package org.tools4j.stacked.index

import java.io.IOException

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.ParseException
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.RAMDirectory

object HelloLucene {
    @Throws(IOException::class, ParseException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        val analyzer = StandardAnalyzer()

        // 1. create the index
        val index = RAMDirectory()

        val config = IndexWriterConfig(analyzer)

        val w = IndexWriter(index, config)
        addDoc(w, "Lucene in Action", "193398817")
        addDoc(w, "Lucene for Dummies", "55320055Z")
        addDoc(w, "Managing Gigabytes", "55063554A")
        addDoc(w, "The Art of Computer Science", "9900333X")
        w.close()

        // 2. query
        val querystr = if (args.size > 0) args[0] else "lucene"

        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        val q = QueryParser("title", analyzer).parse(querystr)

        // 3. search
        val hitsPerPage = 10
        val reader = DirectoryReader.open(index)
        val searcher = IndexSearcher(reader)
        val docs = searcher.search(q, hitsPerPage)
        val hits = docs.scoreDocs

        // 4. display results
        println("Found " + hits.size + " hits.")
        for (i in hits.indices) {
            val docId = hits[i].doc
            val d = searcher.doc(docId)
            println((i + 1).toString() + ". " + d.get("isbn") + "\t" + d.get("title"))
        }

        // reader can only be closed when there
        // is no need to access the documents any more.
        reader.close()
    }

    private fun addPost(w: IndexWriter, post: RawPostImpl) {
        val doc = Document()
        doc.add(NumericDocValuesField("id", (post.id as Long)))
        doc.add(TextField("title", post.title, Field.Store.YES))
        doc.add(TextField("body", post.body, Field.Store.YES))
        doc.add(TextField("tags", post.tags, Field.Store.YES))
        doc.add(StoredField("parentId", post.parentId))
        w.addDocument(doc)
    }

    private fun addDoc(w: IndexWriter, title: String, isbn: String) {
        val doc = Document()
        doc.add(TextField("title", title, Field.Store.YES))

        // use a string field for isbn because we don't want it tokenized
        doc.add(StringField("isbn", isbn, Field.Store.YES))
        w.addDocument(doc)
    }
}