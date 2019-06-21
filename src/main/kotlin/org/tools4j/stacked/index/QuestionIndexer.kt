package org.tools4j.stacked.index

import mu.KLogging
import org.apache.lucene.document.Document

class QuestionIndexer(val stagingIndexes: StagingIndexes,
                      val indexedSiteId: String,
                      val questionIndex: QuestionIndex,
                      val jobStatus: JobStatus = JobStatusImpl()) {

    companion object: KLogging()
    fun index(){
        jobStatus.addOperation("Fetching parent posts")
        val startMs = System.currentTimeMillis()
        val questionsDocIds = stagingIndexes.stagingPostIndex.docIndex.docIdIndex.searchByTerm("isQuestion", "true")
        jobStatus.addOperation("Joining posts, comments and users into question blocks for fast searching")
        var index = 0;

        questionsDocIds.forEach { questionDocId ->
            val questionPost = stagingIndexes.stagingPostIndex.getByDocId(questionDocId)!!
            val questionComments = stagingIndexes.stagingCommentIndex.getByPostId(questionPost.id)
            val answerPosts = stagingIndexes.stagingPostIndex.getByParentId(questionPost.id)
            val answerComments = answerPosts.flatMap { stagingIndexes.stagingCommentIndex.getByPostId(it.id) }
            val userUids = ArrayList<String>()

            userUids.addAll(answerPosts.map { it.userId }.filterNotNull())
            userUids.addAll(answerComments.map { it.userId }.filterNotNull())
            userUids.addAll(questionComments.map { it.userId }.filterNotNull())
            if(questionPost.userId != null) userUids.add(questionPost.userId!!)

            val users = stagingIndexes.stagingUserIndex.getByIds(userUids)
            val usersById = users.map { it.id to it }.toMap()
            val documents = ArrayList<Document>()

            var aggregatedTextContent = if(questionPost.body != null) stripHtmlTagsAndMultiWhitespace(questionPost.body) + "\n" else ""
            aggregatedTextContent += answerPosts.filter{it.body != null}.map { stripHtmlTagsAndMultiWhitespace(it.body!!)}.joinToString("\n") + "\n"
            aggregatedTextContent += questionComments.filter{it.text != null}.map {it.text}.joinToString("\n") + "\n"
            aggregatedTextContent += answerComments.filter{it.text != null}.map {it.text}.joinToString("\n") + "\n"

            documents.addAll(answerPosts.map { it.convertToAnswerDocument(indexedSiteId, usersById[it.userId]) })
            documents.addAll(questionComments.map { it.convertToDocument(indexedSiteId, usersById[it.userId]) })
            documents.addAll(answerComments.map { it.convertToDocument(indexedSiteId, usersById[it.userId]) })
            documents.add(questionPost.convertToQuestionDocument(indexedSiteId, usersById[questionPost.userId], answerPosts.size, aggregatedTextContent))

            questionIndex.addDocsAsBlock(documents)
            jobStatus.currentOperationProgress = "Joined $index of ${questionsDocIds.size} questions"
            index++
        }
        questionIndex.commit()
        questionIndex.onNewDataAddedToIndex()
        jobStatus.addOperation("Finished creating question blocks for site, took ${System.currentTimeMillis() - startMs}ms")
    }
}

