package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class StagingCommentIndexTest {
    private lateinit var stagingCommentIndex: StagingCommentIndex
    private val s1 = CoffeeStagingAssertions()

    @BeforeEach
    fun setup(){
        stagingCommentIndex = coffeeSiteIndexUtils.createAndLoadCommentIndex()
    }

    @Test
    fun testGetCommentsByPostId() {
        val results = stagingCommentIndex.getByPostId("1")
        assertThat(results).hasSize(5)
        s1.assertHasComment4(results);
        s1.assertHasComment6(results);
        s1.assertHasComment7(results);
        s1.assertHasComment8(results);
        s1.assertHasComment9(results);
    }

    @Test
    fun testPurge() {
        assertThat(stagingCommentIndex.getAll()).hasSize(9)
        stagingCommentIndex.purge()
        assertThat(stagingCommentIndex.getAll()).isEmpty()
    }
}