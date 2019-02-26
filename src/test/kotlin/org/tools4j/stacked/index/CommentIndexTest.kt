package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CommentIndexTest {
    private lateinit var commentIndex: CommentIndex

    @BeforeEach
    fun setup(){
        commentIndex = createCommentIndex()
    }

    @Test
    fun testQueryAllCommentsFromIndex() {
        val results = commentIndex.search("question")
        assertThat(results).hasSize(1)
        assertHasComment7(results);
    }

    @Test
    fun testGetCommentsByPostId() {
        val results = commentIndex.getByPostId("1")!!
        assertThat(results).hasSize(5)
        assertHasComment4(results);
        assertHasComment6(results);
        assertHasComment7(results);
        assertHasComment8(results);
        assertHasComment9(results);
    }
}