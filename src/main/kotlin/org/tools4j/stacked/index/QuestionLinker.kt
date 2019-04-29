package org.tools4j.stacked.index

import mu.KLogging
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StoredField
import org.apache.lucene.document.StringField

class QuestionLinker(val stagingIndexes: StagingIndexes, val questionIndex: QuestionIndex) {
    companion object: KLogging()

    fun join(){
        stagingIndexes.postIndex.forEachElementInIndex { post ->
            if(post.parentUid == null){
                val question = post
                val questionMessages = stagingIndexes.commentIndex.getByPostUid(question.uid)
                val answers = stagingIndexes.postIndex.getByParentUid(question.uid)
                val answerMessages = answers.flatMap { stagingIndexes.commentIndex.getByPostUid(it.uid) }
                val userUids = ArrayList<String>()
                userUids.addAll(answers.map { it.ownerUserUid }.filterNotNull().toList())
                userUids.addAll(questionMessages.map { it.userUid }.filterNotNull().toList())
                if(question.ownerUserUid != null) userUids.add(question.ownerUserUid!!)
                val users = stagingIndexes.userIndex.getByUids(userUids)
                val usersByUid = users.map { it.uid to it }.toMap()

                logger.debug { "Joining ${question.uid}" }
                val documents = ArrayList<Document>()
                documents.addAll(answers.map { addUserInfo(usersByUid, addChildFlag(true, addType("answer", it.convertToDocument()))) }.toList());
                documents.addAll(questionMessages.map { addUserInfo(usersByUid, addChildFlag(true, addType("comment", it.convertToDocument()))) }.toList())
                documents.addAll(answerMessages.map { addUserInfo(usersByUid, addChildFlag(true, addType("comment", it.convertToDocument()))) }.toList())
                documents.add(addUserInfo(usersByUid, addChildFlag(false, addType("question", question.convertToDocument()))))
                questionIndex.addDocsAsBlock(documents)
            }
        }
    }

    private fun addUserInfo(usersByUid: Map<String, User>, doc: Document): Document {
        val userUid = if(doc.get("ownerUserUid") != null) doc.get("ownerUserUid") else doc.get("userUid");
        if( userUid != null && usersByUid.containsKey(userUid)){
            val user = usersByUid.get(userUid)!!
            doc.add(StringField("userUid", user.uid, Field.Store.NO))
            if(user.reputation != null) doc.add(StoredField("userReputation", user.reputation))
            if(user.displayName != null) doc.add(StoredField("userDisplayName", user.displayName))
        }
        return doc
    }

    private fun addType(type: String, doc: Document): Document {
        doc.add(StringField("type", type, Field.Store.YES))
        return doc
    }

    private fun addChildFlag(isChild: Boolean, doc: Document): Document {
        doc.add(StringField("child", if(isChild) "Y" else "N", Field.Store.YES))
        return doc
    }
}

