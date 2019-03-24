package org.tools4j.stacked.index

import java.lang.IllegalStateException

class PostService(
    private val postIndex: PostIndex,
    private val commentIndex: CommentIndex,
    private val userIndex: UserIndex) {

    fun search(searchText: String): Set<Question>{
        val rawPosts = postIndex.search(searchText)
        val rawComments = commentIndex.search(searchText)

        val questionsFromPosts = getQuestionsForRawPosts(rawPosts)
        val questionsFromComments = getQuestionsForComments(rawComments, questionsFromPosts)

        val allQuestions = LinkedHashSet<Question>()
        allQuestions.addAll(questionsFromPosts)
        allQuestions.addAll(questionsFromComments)
        return allQuestions
    }

    fun getQuestionsForRawPosts(
        rawPosts: List<RawPost>,
        questionsToIgnore: Set<Question> = emptySet()): Set<Question>{

        val questionsToReturn = LinkedHashSet<Question>()
        for (rawPost in rawPosts) {
            if(questionsToReturn.any{it.containsPost(rawPost.uid)}) continue
            if(questionsToIgnore.any{it.containsPost(rawPost.uid)}) continue
            val question = getQuestion(rawPost.uid) ?: throw IllegalStateException("Cannot find question for post with id ${rawPost.uid}")
            questionsToReturn.add(question)
        }
        return questionsToReturn
    }

    fun getQuestionsForComments(
        rawComments: List<RawComment>,
        questionsToIgnore: Set<Question> = emptySet()): Set<Question>{

        val questionsToReturn = LinkedHashSet<Question>()
        for (rawComment in rawComments) {
            if(questionsToReturn.any{it.containsComment(rawComment.uid)}) continue;
            if(questionsToIgnore.any{it.containsComment(rawComment.uid)}) continue
            val question = getQuestion(rawComment.postUid!!) ?: throw IllegalStateException("Cannot find question for post with uid ${rawComment.uid}")
            questionsToReturn.add(question)
        }
        return questionsToReturn
    }

    fun getQuestion(uid: String): Question?{
        val post = getPost(uid)
        if(post == null) return null
        else if(post.parentUid != null) return getQuestion(post.parentUid!!);
        else {
            val childPosts = postIndex.getByParentUid(uid)
                .map { convertRawPostToPost(it) }
                .toList()
            return QuestionImpl(post, childPosts)
        }
    }

    fun getPost(uid: String): Post?{
        val rawPost = postIndex.getByUid(uid)
        if(rawPost == null) return null
        return convertRawPostToPost(rawPost)
    }

    private fun convertRawPostToPost(rawPost: RawPost): Post {
        val ownerUser = userIndex
            .getByUid(rawPost.ownerUserUid!!)!!

        val comments = commentIndex
            .getByPostUid(rawPost.uid)
            .map { convertRawCommentToComment(it) }
            .toList()
        return PostImpl(rawPost, ownerUser, comments)
    }

    private fun convertRawCommentToComment(rawComment: RawComment): Comment {
        val user = if(rawComment.userUid != null) userIndex.getByUid(rawComment.userUid!!) else null
        return CommentImpl(rawComment, user)
    }
}