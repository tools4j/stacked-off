package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PostIndexTest {
    private lateinit var postIndex: PostIndex

    @BeforeEach
    fun setup() {
        postIndex = createPostIndex()
    }

    @Test
    fun testQueryAllPostsFromIndex() {
        val results = postIndex.search("coffee")
        assertThat(results).hasSize(3)
        assertHasRawPost1(results);
        assertHasRawPost2(results);
        assertHasRawPost3(results);
    }

    @Test
    fun testGetByParentPostId() {
        val results = postIndex.getByParentPostId("1")
        assertThat(results).hasSize(1)
        assertHasRawPost3(results);
    }

    @Test
    fun testGetByParentPostId_noPosts() {
        val results = postIndex.getByParentPostId("2")
        assertThat(results).isEmpty()
    }
}