package org.tools4j.stacked

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
}