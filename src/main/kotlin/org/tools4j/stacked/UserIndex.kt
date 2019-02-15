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

class UserIndex(val indexFactory: IndexFactory) {
    private val name = "users"
    private lateinit var index: Directory
    private lateinit var analyzer: Analyzer

    fun init() {
        index = indexFactory.createIndex(name)
        analyzer = StandardAnalyzer()
    }

    fun addUsers(users: List<User>){
        val config = IndexWriterConfig(analyzer)
        val w = IndexWriter(index, config)
        for (user in users) {
            addUser(w, user)
        }
        w.close()
    }

    public User getUserById(){

    }

    private fun createUserFromDocument(doc: Document):User {
        return UserImpl(
            doc.get("id"),
            doc.get("reputation"),
            doc.get("displayName"),
            doc.get("accountId"))
    }

    private fun addUser(w: IndexWriter, user: User) {
        val doc = Document()
        doc.add(StringField("id", user.id!!, Field.Store.YES))
        if(user.reputation != null) doc.add(StoredField("reputation", user.reputation))
        if(user.displayName != null) doc.add(StoredField("displayName", user.displayName))
        if(user.accountId != null) doc.add(StoredField("accountId", user.accountId))
        w.addDocument(doc)
    }
}