package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PostServiceTest {
    private lateinit var postService: PostService

    @BeforeEach
    fun setup() {
        postService = createPostService()
    }

    @Test
    fun testGetPost() {
        val post = postService.getPost("2")
        assertIsPost2(post!!)
    }

    @Test
    fun testGetQuestion1() {
        val question = postService.getQuestion("1")
        assertIsQuestion1(question!!)
    }

    @Test
    fun testGetQuestion2() {
        val question = postService.getQuestion("2")
        assertIsQuestion2(question!!)
    }

    @Test
    fun testGetQuestion3() {
        val question = postService.getQuestion("3")
        //Parent of post 3 is post 1
        assertIsQuestion1(question!!)
    }

    @Test
    fun testGetQuestion_thatDoesNotExist() {
        assertThat(postService.getQuestion("4")).isNull()
    }

    @Test
    fun testGetQuestionsForComments_emptyComments(){
        assertThat(postService.getQuestionsForComments(emptyList())).isEmpty()
    }

    @Test
    fun testGetQuestionsForComments(){
        val commentsIndex = createCommentIndex()
        val comment1 = commentsIndex.getById("1")!!
        val questions = postService.getQuestionsForComments(listOf(comment1))
        assertHasQuestion1(questions)
    }

    @Test
    fun testGetQuestionsForComments_multipleQuestions(){
        val commentsIndex = createCommentIndex()
        val comment1 = commentsIndex.getById("1")!!
        val comment3 = commentsIndex.getById("3")!!
        val questions = postService.getQuestionsForComments(listOf(comment1, comment3))
        assertHasQuestion1(questions)
        assertHasQuestion2(questions)
    }

    @Test
    fun testGetQuestionsForComments_ignoreAlreadyFetchedQuestions(){
        val commentsIndex = createCommentIndex()

        val question1 =  postService.getQuestion("1")!!
        val comment1 = commentsIndex.getById("1")!!
        val comment3 = commentsIndex.getById("3")!!
        val questions = postService.getQuestionsForComments(
            listOf(comment1, comment3), setOf(question1)
        )
        assertThat(questions).hasSize(1)
        assertHasQuestion2(questions)
    }

    @Test
    fun testGetQuestionsForRawPosts(){
        val postIndex = createPostIndex()
        val rawPost1 = postIndex.getById("1")!!
        val rawPost2 = postIndex.getById("2")!!
        val rawPost3 = postIndex.getById("3")!!

        var questions = postService.getQuestionsForRawPosts(listOf(rawPost1))
        assertThat(questions).hasSize(1)
        assertHasQuestion1(questions)

        questions = postService.getQuestionsForRawPosts(listOf(rawPost1, rawPost2, rawPost3))
        assertThat(questions).hasSize(2)
        assertHasQuestion1(questions)
        assertHasQuestion2(questions)
    }

    @Test
    fun testGetQuestionsForRawPosts_ignoreAlreadyFetchedQuestions(){
        val postIndex = createPostIndex()
        val rawPost1 = postIndex.getById("1")!!
        val rawPost2 = postIndex.getById("2")!!
        val rawPost3 = postIndex.getById("3")!!

        val question1 = postService.getQuestion("1")!!

        val questions = postService.getQuestionsForRawPosts(
            listOf(rawPost1, rawPost2, rawPost3),
            setOf(question1))

        assertThat(questions).hasSize(1)
        assertHasQuestion2(questions)
    }

    @Test
    fun testGetQuestionsForRawPosts_haveAlreadyFetchedAll(){
        val postIndex = createPostIndex()
        val rawPost1 = postIndex.getById("1")!!
        val rawPost2 = postIndex.getById("2")!!
        val rawPost3 = postIndex.getById("3")!!

        val question1 = postService.getQuestion("1")!!
        val question2 = postService.getQuestion("2")!!

        val questions = postService.getQuestionsForRawPosts(
            listOf(rawPost1, rawPost2, rawPost3),
            setOf(question1, question2))

        assertThat(questions).isEmpty()
    }

    @Test
    fun testSearch(){
        val questions = postService.search("coffee")
        assertThat(questions).hasSize(2)
        assertHasQuestion1(questions)
        assertHasQuestion2(questions)
    }

    @Test
    fun testSearch_text_is_in_one_comment_in_post_1(){
        val questions = postService.search("Recommending")
        assertThat(questions).hasSize(1)
        assertHasQuestion1(questions)
    }

    @Test
    fun testSearch_text_is_in_one_comment_in_post_3(){
        val questions = postService.search("rpg.se")
        assertThat(questions).hasSize(1)
        assertHasQuestion1(questions)
    }

    @Test
    fun testSearch_text_is_in_post_3(){
        val questions = postService.search("traditional")
        assertThat(questions).hasSize(1)
        assertHasQuestion1(questions)
    }

    @Test
    fun testSearch_text_is_in_post_2(){
        val questions = postService.search("zero")
        assertThat(questions).hasSize(1)
        assertHasQuestion2(questions)
    }
}
