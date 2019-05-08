package org.tools4j.stacked.index

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach


internal class QuestionIndexTest {
    private lateinit var questionIndex: QuestionIndex
    private val s1 = CoffeeSiteAssertions()
    private val s2 = BeerSiteAssertions()

    @BeforeEach
    fun setup() {
        questionIndex = createAndLoadQuestionIndex()
    }

    @Test
    fun testGetSite1Question1() {
        val question = questionIndex.getQuestionByUid("p${s1.indexedSiteId}.1")
        s1.assertIsQuestion1(question!!)
    }

    @Test
    fun testGetSite1Question2() {
        val question = questionIndex.getQuestionByUid("p${s1.indexedSiteId}.2")
        s1.assertIsQuestion2(question!!)
    }

    @Test
    fun testGetSite1Question_thatDoesNotExist() {
        Assertions.assertThat(questionIndex.getQuestionByUid("p${s1.indexedSiteId}.4")).isNull()
    }

    @Test
    fun testSite1Search(){
        val questions = questionIndex.search("coffee")
        Assertions.assertThat(questions).hasSize(2)
        s1.assertHasQuestion1(questions)
        s1.assertHasQuestion2(questions)
    }

    @Test
    fun testSite2Search(){
        val questions = questionIndex.search("Homebrewing")
        Assertions.assertThat(questions).hasSize(1)
        s2.assertHasQuestion1(questions)
    }

    @Test
    fun testSite1AndSite2Search(){
        val questions = questionIndex.search("blah")
        Assertions.assertThat(questions).hasSize(2)
        s1.assertHasQuestion1(questions)
        s2.assertHasQuestion3(questions)
    }


    @Test
    fun testSite1Search_text_is_in_one_comment_in_post_1(){
        val questions = questionIndex.search("Recommending")
        Assertions.assertThat(questions).hasSize(1)
        s1.assertHasQuestion1(questions)
    }

    @Test
    fun testSite1Search_text_is_in_one_comment_in_post_3(){
        val questions = questionIndex.search("rpg.se")
        Assertions.assertThat(questions).hasSize(1)
        s1.assertHasQuestion1(questions)
    }

    @Test
    fun testSite1Search_text_is_in_post_3(){
        val questions = questionIndex.search("traditional")
        Assertions.assertThat(questions).hasSize(1)
        s1.assertHasQuestion1(questions)
    }

    @Test
    fun testSite1Search_text_is_in_post_2(){
        val questions = questionIndex.search("zero")
        Assertions.assertThat(questions).hasSize(1)
        s1.assertHasQuestion2(questions)
    }
}