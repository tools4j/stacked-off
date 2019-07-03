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
                logger.error("Error during joining", e)
                exceptions.add(e)
                jobs.forEach{it.interrupt()}
            }
            jobs.add(runThread(exceptionHandler){ fetchPosts(questionsDocIds, postsQueue) })
            jobs.add(runThread(exceptionHandler){ fetchComments(postsQueue, postsAndCommentsQueue) })
            jobs.add(runThread(exceptionHandler){ buildDocs(postsAndCommentsQueue, docsQueue) })
            jobs.add(runThread(exceptionHandler){ addDocsToLucene(docsQueue, questionsDocIds.size) })
            jobs.forEach{it.join()}

            if(!exceptions.isEmpty()){
                jobStatus.addOperation("Error occurred during joining")
                throw exceptions.element()
            } else {
                jobStatus.addOperation("Finished creating question blocks for site, took ${System.currentTimeMillis() - startMs}ms")
            }
        }
    }

    private fun runThread(exceptionHandler: (e: Exception) -> Unit, runnable: () -> Unit): Thread {
        val thread = Thread {
            try {
                runnable()
            } catch (e: InterruptedException) {
                logger.warn { "posts proc interrupted" }
            } catch (e: Exception) {
                logger.error("Error during joining proc", e )
                exceptionHandler(e)
            }
        }
        thread.start()
        return thread
    }

    private fun fetchPosts(
        questionsDocIds: List<Int>,
        outputQueue: LinkedBlockingDeque<List<StagingPost>>
    ) {
        questionsDocIds.forEach { questionDocId ->
            val posts = ArrayList<StagingPost>()
            posts.add(stagingIndexes.stagingPostIndex.getByDocId(questionDocId)!!)
            posts.addAll(stagingIndexes.stagingPostIndex.getByParentId(posts.get(0).id))
            outputQueue.put(posts)
        }
        outputQueue.put(emptyList()) //put 'poison pill'
        logger.info { "finished posts proc" }
    }

    private fun fetchComments(
        inputQueue: LinkedBlockingDeque<List<StagingPost>>,
        outputQueue: LinkedBlockingDeque<Optional<Pair<List<StagingPost>, List<StagingComment>>>>
    ){
        while (!Thread.currentThread().isInterrupted) {
            val stagingPosts = inputQueue.take()
            if (stagingPosts.isEmpty()) break  //break if 'poison pill'
            val stagingComments = stagingPosts.flatMap { stagingIndexes.stagingCommentIndex.getByPostId(it.id) }
            outputQueue.put(Optional.of(Pair(stagingPosts, stagingComments)))
        }
        outputQueue.put(Optional.empty()) //put 'poison pill'
        logger.info { "finished comments proc" }
    }

    private fun buildDocs(
        inputQueue: LinkedBlockingDeque<Optional<Pair<List<StagingPost>, List<StagingComment>>>>,
        outputQueue: LinkedBlockingDeque<List<Document>>
    ){
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
        logger.info { "finished user proc" }
    }

    private fun addDocsToLucene(
        inputQueue: LinkedBlockingDeque<List<Document>>,
        totalQuestionCount: Int
    ){
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
        logger.info { "finished docs proc" }
    }
}

