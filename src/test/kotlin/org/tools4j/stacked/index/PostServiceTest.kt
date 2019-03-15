package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PostServiceTest {
    private lateinit var postService: PostService
    private val s1 = CoffeeSiteAssertions()
    private val s2 = BeerSiteAssertions()

    @BeforeEach
    fun setup() {
        postService = createPostService()
    }

    @Test
    fun testGetSite1Post() {
        val post = postService.getPost("${s1.indexedSiteId}.2")
        s1.assertIsPost2(post!!)
    }

    @Test
    fun testGetSite1Question1() {
        val question = postService.getQuestion("${s1.indexedSiteId}.1")
        s1.assertIsQuestion1(question!!)
    }

    @Test
    fun testGetSite1Question2() {
        val question = postService.getQuestion("${s1.indexedSiteId}.2")
        s1.assertIsQuestion2(question!!)
    }

    @Test
    fun testGetSite1Question3() {
        val question = postService.getQuestion("${s1.indexedSiteId}.3")
        //Parent of post 3 is post 1
        s1.assertIsQuestion1(question!!)
    }

    @Test
    fun testGetSite1Question_thatDoesNotExist() {
        assertThat(postService.getQuestion("${s1.indexedSiteId}.4")).isNull()
    }

    @Test
    fun testGetQuestionsForComments_emptyComments(){
        assertThat(postService.getQuestionsForComments(emptyList())).isEmpty()
    }

    @Test
    fun testGetQuestionsForSite1Comments(){
        val commentsIndex = createAndLoadCommentIndex()
        val comment1 = commentsIndex.getByUid("${s1.indexedSiteId}.1")!!
        val questions = postService.getQuestionsForComments(listOf(comment1))
        s1.assertHasQuestion1(questions)
    }

    @Test
    fun testGetQuestionsForSite1Comments_multipleQuestions(){
        val commentsIndex = createAndLoadCommentIndex()
        val comment1 = commentsIndex.getByUid("${s1.indexedSiteId}.1")!!
        val comment3 = commentsIndex.getByUid("${s1.indexedSiteId}.3")!!
        val questions = postService.getQuestionsForComments(listOf(comment1, comment3))
        s1.assertHasQuestion1(questions)
        s1.assertHasQuestion2(questions)
    }

    @Test
    fun testGetQuestionsForSite1Comments_ignoreAlreadyFetchedQuestions(){
        val commentsIndex = createAndLoadCommentIndex()

        val question1 =  postService.getQuestion("${s1.indexedSiteId}.1")!!
        val comment1 = commentsIndex.getByUid("${s1.indexedSiteId}.1")!!
        val comment3 = commentsIndex.getByUid("${s1.indexedSiteId}.3")!!
        val questions = postService.getQuestionsForComments(
            listOf(comment1, comment3), setOf(question1)
        )
        assertThat(questions).hasSize(1)
        s1.assertHasQuestion2(questions)
    }

    @Test
    fun testGetSite1QuestionsForRawPosts(){
        val postIndex = createAndLoadPostIndex()
        val rawPost1 = postIndex.getByUid("${s1.indexedSiteId}.1")!!
        val rawPost2 = postIndex.getByUid("${s1.indexedSiteId}.2")!!
        val rawPost3 = postIndex.getByUid("${s1.indexedSiteId}.3")!!

        var questions = postService.getQuestionsForRawPosts(listOf(rawPost1))
        assertThat(questions).hasSize(1)
        s1.assertHasQuestion1(questions)

        questions = postService.getQuestionsForRawPosts(listOf(rawPost1, rawPost2, rawPost3))
        assertThat(questions).hasSize(2)
        s1.assertHasQuestion1(questions)
        s1.assertHasQuestion2(questions)
    }

    @Test
    fun testGetSite1QuestionsForRawPosts_ignoreAlreadyFetchedQuestions(){
        val postIndex = createAndLoadPostIndex()
        val rawPost1 = postIndex.getByUid("${s1.indexedSiteId}.1")!!
        val rawPost2 = postIndex.getByUid("${s1.indexedSiteId}.2")!!
        val rawPost3 = postIndex.getByUid("${s1.indexedSiteId}.3")!!

        val question1 = postService.getQuestion("${s1.indexedSiteId}.1")!!

        val questions = postService.getQuestionsForRawPosts(
            listOf(rawPost1, rawPost2, rawPost3),
            setOf(question1))

        assertThat(questions).hasSize(1)
        s1.assertHasQuestion2(questions)
    }

    @Test
    fun testGetSite1QuestionsForRawPosts_haveAlreadyFetchedAll(){
        val postIndex = createAndLoadPostIndex()
        val rawPost1 = postIndex.getByUid("${s1.indexedSiteId}.1")!!
        val rawPost2 = postIndex.getByUid("${s1.indexedSiteId}.2")!!
        val rawPost3 = postIndex.getByUid("${s1.indexedSiteId}.3")!!

        val question1 = postService.getQuestion("${s1.indexedSiteId}.1")!!
        val question2 = postService.getQuestion("${s1.indexedSiteId}.2")!!

        val questions = postService.getQuestionsForRawPosts(
            listOf(rawPost1, rawPost2, rawPost3),
            setOf(question1, question2))

        assertThat(questions).isEmpty()
    }

    @Test
    fun testSite1Search(){
        val questions = postService.search("coffee")
        assertThat(questions).hasSize(2)
        s1.assertHasQuestion1(questions)
        s1.assertHasQuestion2(questions)
    }

    @Test
    fun testSite2Search(){
        val questions = postService.search("Homebrewing")
        assertThat(questions).hasSize(1)
        s2.assertHasQuestion1(questions)
    }

    @Test
    fun testSite1AndSite2Search(){
        val questions = postService.search("blah")
        assertThat(questions).hasSize(2)
        s1.assertHasQuestion1(questions)
        s2.assertHasQuestion3(questions)
    }


    @Test
    fun testSite1Search_text_is_in_one_comment_in_post_1(){
        val questions = postService.search("Recommending")
        assertThat(questions).hasSize(1)
        s1.assertHasQuestion1(questions)
    }

    @Test
    fun testSite1Search_text_is_in_one_comment_in_post_3(){
        val questions = postService.search("rpg.se")
        assertThat(questions).hasSize(1)
        s1.assertHasQuestion1(questions)
    }

    @Test
    fun testSite1Search_text_is_in_post_3(){
        val questions = postService.search("traditional")
        assertThat(questions).hasSize(1)
        s1.assertHasQuestion1(questions)
    }

    @Test
    fun testSite1Search_text_is_in_post_2(){
        val questions = postService.search("zero")
        assertThat(questions).hasSize(1)
        s1.assertHasQuestion2(questions)
    }
}
