package org.tools4j.stacked.index

import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.apache.lucene.document.Document
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingDeque

class QuestionIndexer(val stagingIndexes: StagingIndexes,
                      val indexedSiteId: String,
                      val questionIndex: QuestionIndex,
                      val jobStatus: JobStatus = JobStatusImpl()) {

    companion object : KLogging()

    fun index() {
        runBlocking {
            jobStatus.addOperation("Fetching parent posts")
            val startMs = System.currentTimeMillis()
            val questionsDocIds = stagingIndexes.stagingPostIndex.docIndex.docIdIndex.searchByTerm(
                "isQuestion",
                "true",
                UnscoredCollector(false)
            )
            jobStatus.addOperation("Joining posts, comments and users into question blocks for fast searching")

            val exceptions = ConcurrentLinkedQueue<Throwable>()
            val postsQueue = LinkedBlockingDeque<List<StagingPost>>(1000)
            val postsAndCommentsQueue = LinkedBlockingDeque<Optional<Pair<List<StagingPost>, List<StagingComment>>>>(1000)
            val docsQueue = LinkedBlockingDeque<List<Document>>(1000)

            val jobs = ArrayList<Thread>()
            val exceptionHandler = {e: Exception ->
                logger.error { "Error during joining: $e" }
                exceptions.add(e)
                jobs.forEach{it.interrupt()}
            }
            jobs.add(Thread { fetchPosts(questionsDocIds, postsQueue, exceptionHandler) })
            jobs.add(Thread { fetchComments(postsQueue, postsAndCommentsQueue, exceptionHandler) })
            jobs.add(Thread { buildDocs(postsAndCommentsQueue, docsQueue, exceptionHandler) })
            jobs.add(Thread { addDocsToLucene(docsQueue, questionsDocIds.size, exceptionHandler) })
            jobs.forEach{it.start()}
            jobs.forEach{it.join()}

            if(!exceptions.isEmpty()){
                jobStatus.addOperation("Error occurred during joining")
                throw exceptions.element()
            } else {
                jobStatus.addOperation("Finished creating question blocks for site, took ${System.currentTimeMillis() - startMs}ms")
            }
        }
    }

    private fun fetchPosts(
        questionsDocIds: List<Int>,
        outputQueue: LinkedBlockingDeque<List<StagingPost>>,
        exceptionHandler: (Exception) -> Unit
    ) {
        try {
            questionsDocIds.forEach { questionDocId ->
                val posts = ArrayList<StagingPost>()
                posts.add(stagingIndexes.stagingPostIndex.getByDocId(questionDocId)!!)
                posts.addAll(stagingIndexes.stagingPostIndex.getByParentId(posts.get(0).id))
                outputQueue.put(posts)
            }
            outputQueue.put(emptyList()) //put 'poison pill'
        } catch (e: InterruptedException){
            logger.warn { "posts proc interrupted" }
        } catch (e: Exception) {
            logger.error { "Error during posts proc ${e.message}" }
            exceptionHandler(e)
        }
        logger.info { "finished posts proc" }
    }

    private fun fetchComments(
        inputQueue: LinkedBlockingDeque<List<StagingPost>>,
        outputQueue: LinkedBlockingDeque<Optional<Pair<List<StagingPost>, List<StagingComment>>>>,
        exceptionHandler: (Exception) -> Unit
    ){
        try {
            while (!Thread.currentThread().isInterrupted) {
                val stagingPosts = inputQueue.take()
                if (stagingPosts.isEmpty()) break  //break if 'poison pill'
                val stagingComments = stagingPosts.flatMap { stagingIndexes.stagingCommentIndex.getByPostId(it.id) }
                outputQueue.put(Optional.of(Pair(stagingPosts, stagingComments)))
            }
            outputQueue.put(Optional.empty()) //put 'poison pill'
        } catch (e: InterruptedException){
            logger.warn { "comments proc interrupted" }
        } catch (e: Exception) {
            logger.error { "Error during comments proc ${e.message}" }
            exceptionHandler(e)
        }
        logger.info { "finished comments proc" }
    }

    private fun buildDocs(
        inputQueue: LinkedBlockingDeque<Optional<Pair<List<StagingPost>, List<StagingComment>>>>,
        outputQueue: LinkedBlockingDeque<List<Document>>,
        exceptionHandler: (Exception) -> Unit
    ){
        try {
            while (!Thread.currentThread().isInterrupted) {
                val postsAndCommentsOptional = inputQueue.take()
                if (!postsAndCommentsOptional.isPresent) break  //break if 'poison pill'
                val postsAndComments = postsAndCommentsOptional.get()
                val stagingPosts = postsAndComments.first
                val comments = postsAndComments.second
                val questionPost = stagingPosts.first()
                val answerPosts = stagingPosts.subList(1, stagingPosts.size)

                val userUids = ArrayList<String>()
                userUids.addAll(stagingPosts.map { it.userId }.filterNotNull())
                userUids.addAll(comments.map { it.userId }.filterNotNull())
                if (questionPost.userId != null) userUids.add(questionPost.userId)

                val users = stagingIndexes.stagingUserIndex.getByIds(userUids)
                val usersById = users.map { it.id to it }.toMap()
                val documents = ArrayList<Document>()

                var aggregatedTextContent = if (questionPost.body != null) stripHtmlTagsAndMultiWhitespace(questionPost.body) + "\n" else ""
                aggregatedTextContent += stagingPosts.filter { it.body != null }.map { stripHtmlTagsAndMultiWhitespace(it.body!!) }.joinToString("\n") + "\n"
                aggregatedTextContent += comments.filter { it.text != null }.map { it.text }.joinToString("\n") + "\n"

                documents.addAll(answerPosts.map { it.convertToAnswerDocument(indexedSiteId, usersById[it.userId]) })
                documents.addAll(comments.map { it.convertToDocument(indexedSiteId, usersById[it.userId]) })
                documents.add(questionPost.convertToQuestionDocument(indexedSiteId, usersById[questionPost.userId], answerPosts.size, aggregatedTextContent))
                outputQueue.put(documents)
            }
            outputQueue.put(emptyList()) //put 'poison pill'
        } catch (e: InterruptedException){
            logger.warn { "user proc interrupted" }
        } catch (e: Exception) {
            logger.error { "Error during user proc ${e.message}" }
            exceptionHandler(e)
        }
        logger.info { "finished user proc" }
    }

    private fun addDocsToLucene(
        inputQueue: LinkedBlockingDeque<List<Document>>,
        totalQuestionCount: Int,
        exceptionHandler: (Exception) -> Unit
    ){
        try {
            var index = 1;
            while (!Thread.currentThread().isInterrupted) {
                val documents = inputQueue.take()
                if (documents.isEmpty()) break  //break if 'poison pill'
                questionIndex.addDocsAsBlock(documents)
                jobStatus.currentOperationProgress = "Joined $index of ${totalQuestionCount} questions"
                index++
            }
            questionIndex.commit()
            questionIndex.onIndexDataChange()
        } catch (e: InterruptedException){
            logger.warn { "docs proc interrupted" }
        } catch (e: Exception) {
            logger.error { "Error during docs proc ${e.message}" }
            exceptionHandler(e)
        }
        logger.info { "finished docs proc" }
    }
}

