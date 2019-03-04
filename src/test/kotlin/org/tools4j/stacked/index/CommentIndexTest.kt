package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CommentIndexTest {
    private lateinit var commentIndex: CommentIndex
    private val s1 = Site1Assertions()

    @BeforeEach
    fun setup(){
        commentIndex = createAndLoadCommentIndex()
    }

    @Test
    fun testQueryAllCommentsFromIndex() {
        val results = commentIndex.search("question")
        assertThat(results).hasSize(1)
        s1.assertHasComment7(results);
    }

    @Test
    fun testGetCommentsByPostId() {
        val results = commentIndex.getByPostUid("$SITE_1.1")!!
        assertThat(results).hasSize(5)
        s1.assertHasComment4(results);
        s1.assertHasComment6(results);
        s1.assertHasComment7(results);
        s1.assertHasComment8(results);
        s1.assertHasComment9(results);
    }
}