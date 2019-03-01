package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PostServiceTest {
    private lateinit var postService: PostService
    private val s1 = Site1Assertions()

    @BeforeEach
    fun setup() {
        postService = createPostService()
    }

    @Test
    fun testGetPost() {
        val post = postService.getPost("$SITE_1.2")
        s1.assertIsPost2(post!!)
    }

    @Test
    fun testGetQuestion1() {
        val question = postService.getQuestion("$SITE_1.1")
        s1.assertIsQuestion1(question!!)
    }

    @Test
    fun testGetQuestion2() {
        val question = postService.getQuestion("$SITE_1.2")
        s1.assertIsQuestion2(question!!)
    }

    @Test
    fun testGetQuestion3() {
        val question = postService.getQuestion("$SITE_1.3")
        //Parent of post 3 is post 1
        s1.assertIsQuestion1(question!!)
    }

    @Test
    fun testGetQuestion_thatDoesNotExist() {
        assertThat(postService.getQuestion("$SITE_1.4")).isNull()
    }

    @Test
    fun testGetQuestionsForComments_emptyComments(){
        assertThat(postService.getQuestionsForComments(emptyList())).isEmpty()
    }

    @Test
    fun testGetQuestionsForComments(){
        val commentsIndex = createCommentIndex()
        val comment1 = commentsIndex.getByUid("$SITE_1.1")!!
        val questions = postService.getQuestionsForComments(listOf(comment1))
        s1.assertHasQuestion1(questions)
    }

    @Test
    fun testGetQuestionsForComments_multipleQuestions(){
        val commentsIndex = createCommentIndex()
        val comment1 = commentsIndex.getByUid("$SITE_1.1")!!
        val comment3 = commentsIndex.getByUid("$SITE_1.3")!!
        val questions = postService.getQuestionsForComments(listOf(comment1, comment3))
        s1.assertHasQuestion1(questions)
        s1.assertHasQuestion2(questions)
    }

    @Test
    fun testGetQuestionsForComments_ignoreAlreadyFetchedQuestions(){
        val commentsIndex = createCommentIndex()

        val question1 =  postService.getQuestion("$SITE_1.1")!!
        val comment1 = commentsIndex.getByUid("$SITE_1.1")!!
        val comment3 = commentsIndex.getByUid("$SITE_1.3")!!
        val questions = postService.getQuestionsForComments(
            listOf(comment1, comment3), setOf(question1)
        )
        assertThat(questions).hasSize(1)
        s1.assertHasQuestion2(questions)
    }

    @Test
    fun testGetQuestionsForRawPosts(){
        val postIndex = createPostIndex()
        val rawPost1 = postIndex.getByUid("$SITE_1.1")!!
        val rawPost2 = postIndex.getByUid("$SITE_1.2")!!
        val rawPost3 = postIndex.getByUid("$SITE_1.3")!!

        var questions = postService.getQuestionsForRawPosts(listOf(rawPost1))
        assertThat(questions).hasSize(1)
        s1.assertHasQuestion1(questions)

        questions = postService.getQuestionsForRawPosts(listOf(rawPost1, rawPost2, rawPost3))
        assertThat(questions).hasSize(2)
        s1.assertHasQuestion1(questions)
        s1.assertHasQuestion2(questions)
    }

    @Test
    fun testGetQuestionsForRawPosts_ignoreAlreadyFetchedQuestions(){
        val postIndex = createPostIndex()
        val rawPost1 = postIndex.getByUid("$SITE_1.1")!!
        val rawPost2 = postIndex.getByUid("$SITE_1.2")!!
        val rawPost3 = postIndex.getByUid("$SITE_1.3")!!

        val question1 = postService.getQuestion("$SITE_1.1")!!

        val questions = postService.getQuestionsForRawPosts(
            listOf(rawPost1, rawPost2, rawPost3),
            setOf(question1))

        assertThat(questions).hasSize(1)
        s1.assertHasQuestion2(questions)
    }

    @Test
    fun testGetQuestionsForRawPosts_haveAlreadyFetchedAll(){
        val postIndex = createPostIndex()
        val rawPost1 = postIndex.getByUid("$SITE_1.1")!!
        val rawPost2 = postIndex.getByUid("$SITE_1.2")!!
        val rawPost3 = postIndex.getByUid("$SITE_1.3")!!

        val question1 = postService.getQuestion("$SITE_1.1")!!
        val question2 = postService.getQuestion("$SITE_1.2")!!

        val questions = postService.getQuestionsForRawPosts(
            listOf(rawPost1, rawPost2, rawPost3),
            setOf(question1, question2))

        assertThat(questions).isEmpty()
    }

    @Test
    fun testSearch(){
        val questions = postService.search("coffee")
        assertThat(questions).hasSize(2)
        s1.assertHasQuestion1(questions)
        s1.assertHasQuestion2(questions)
    }

    @Test
    fun testSearch_text_is_in_one_comment_in_post_1(){
        val questions = postService.search("Recommending")
        assertThat(questions).hasSize(1)
        s1.assertHasQuestion1(questions)
    }

    @Test
    fun testSearch_text_is_in_one_comment_in_post_3(){
        val questions = postService.search("rpg.se")
        assertThat(questions).hasSize(1)
        s1.assertHasQuestion1(questions)
    }

    @Test
    fun testSearch_text_is_in_post_3(){
        val questions = postService.search("traditional")
        assertThat(questions).hasSize(1)
        s1.assertHasQuestion1(questions)
    }

    @Test
    fun testSearch_text_is_in_post_2(){
        val questions = postService.search("zero")
        assertThat(questions).hasSize(1)
        s1.assertHasQuestion2(questions)
    }
}
