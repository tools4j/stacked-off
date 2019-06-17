package org.tools4j.stacked.index

import mu.KLogging
import org.apache.lucene.document.Document

class QuestionIndexer(val stagingIndexes: StagingIndexes,
                      val indexedSiteId: String,
                      val questionIndex: QuestionIndex,
                      val jobStatus: JobStatus = JobStatusImpl()) {

    companion object: KLogging()
    fun index(){
        jobStatus.addOperation("Joining posts, comments and users into question blocks for fast searching")
        stagingIndexes.stagingPostIndex.forEachElementInIndex { post, index, total ->
            if(post.parentId == null){
                val question = post
                val questionComments = stagingIndexes.stagingCommentIndex.getByPostId(question.id)
                val answers = stagingIndexes.stagingPostIndex.getByParentId(question.id)
                val answerComments = answers.flatMap { stagingIndexes.stagingCommentIndex.getByPostId(it.id) }
                val userUids = ArrayList<String>()

                userUids.addAll(answers.map { it.userId }.filterNotNull())
                userUids.addAll(answerComments.map { it.userId }.filterNotNull())
                userUids.addAll(questionComments.map { it.userId }.filterNotNull())
                if(question.userId != null) userUids.add(question.userId!!)

                val users = stagingIndexes.stagingUserIndex.getByIds(userUids)
                val usersById = users.map { it.id to it }.toMap()
                val documents = ArrayList<Document>()

                documents.addAll(answers.map { it.convertToAnswerDocument(indexedSiteId, usersById[it.userId]) })
                documents.addAll(questionComments.map { it.convertToDocument(indexedSiteId, usersById[it.userId]) })
                documents.addAll(answerComments.map { it.convertToDocument(indexedSiteId, usersById[it.userId]) })
                documents.add(question.convertToQuestionDocument(indexedSiteId, usersById[question.userId], answers.size))

                questionIndex.addDocsAsBlock(documents)
                jobStatus.currentOperationProgress = "Joined $index of $total posts"
            }
        }
        questionIndex.commit()
        questionIndex.onNewDataAddedToIndex()
        jobStatus.addOperation("Finished creating question blocks for site")
    }
}

