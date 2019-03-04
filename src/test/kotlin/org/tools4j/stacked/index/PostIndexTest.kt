package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class PostIndexTest {
    private lateinit var postIndex: PostIndex
    private val s1 = Site1Assertions()

    @BeforeEach
    fun setup() {
        postIndex = createAndLoadPostIndex()
    }

    @Test
    fun testQueryAllPostsFromIndex() {
        val results = postIndex.search("coffee")
        assertThat(results).hasSize(3)
        s1.assertHasRawPost1(results);
        s1.assertHasRawPost2(results);
        s1.assertHasRawPost3(results);
    }

    @Test
    fun testGetByParentPostId() {
        val results = postIndex.getByParentUid("$SITE_1.1")
        assertThat(results).hasSize(1)
        s1.assertHasRawPost3(results);
    }

    @Test
    fun testGetByParentPostId_noPosts() {
        val results = postIndex.getByParentUid("$SITE_1.2")
        assertThat(results).isEmpty()
    }
}