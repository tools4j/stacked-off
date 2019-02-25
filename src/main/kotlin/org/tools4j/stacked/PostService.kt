package org.tools4j.stacked

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
            if(questionsToReturn.any{it.containsPost(rawPost.id)}) continue
            if(questionsToIgnore.any{it.containsPost(rawPost.id)}) continue
            val question = getQuestion(rawPost.id) ?: throw IllegalStateException("Cannot find question for post with id ${rawPost.id}")
            questionsToReturn.add(question)
        }
        return questionsToReturn
    }

    fun getQuestionsForComments(
        rawComments: List<RawComment>,
        questionsToIgnore: Set<Question> = emptySet()): Set<Question>{

        val questionsToReturn = LinkedHashSet<Question>()
        for (rawComment in rawComments) {
            if(questionsToReturn.any{it.containsComment(rawComment.id)}) continue;
            if(questionsToIgnore.any{it.containsComment(rawComment.id)}) continue
            val question = getQuestion(rawComment.postId!!) ?: throw IllegalStateException("Cannot find question for post with id ${rawComment.id}")
            questionsToReturn.add(question)
        }
        return questionsToReturn
    }

    fun getQuestion(id: String): Question?{
        val post = getPost(id)
        if(post == null) return null
        else if(post.parentId != null) return getQuestion(post.parentId!!);
        else {
            val childPosts = postIndex.getByParentPostId(id)
                .map { convertRawPostToPost(it) }
                .toList()
            return QuestionImpl(post, childPosts)
        }
    }

    fun getPost(id: String): Post?{
        val rawPost = postIndex.getById(id)
        if(rawPost == null) return null
        return convertRawPostToPost(rawPost)
    }

    private fun convertRawPostToPost(rawPost: RawPost): Post {
        val comments = commentIndex
            .getByPostId(rawPost.id)
            .map { convertRawCommentToComment(it) }
            .toList()
        return PostImpl(rawPost, comments)
    }

    private fun convertRawCommentToComment(rawComment: RawComment): Comment {
        val user = if(rawComment.userId != null) userIndex.getById(rawComment.userId!!) else null
        return CommentImpl(rawComment, user)
    }
}