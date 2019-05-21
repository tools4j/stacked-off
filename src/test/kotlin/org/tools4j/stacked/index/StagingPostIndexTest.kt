package org.tools4j.stacked.index

import org.apache.lucene.index.Term
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class StagingPostIndexTest {
    private lateinit var stagingPostIndex: StagingPostIndex
    private val s1 = CoffeeStagingAssertions()

    @BeforeEach
    fun setup() {
        stagingPostIndex = coffeeSiteIndexUtils.createAndLoadPostIndex()
    }

    @Test
    fun testGetByParentPostId() {
        val results = stagingPostIndex.getByParentId("1")
        assertThat(results).hasSize(1)
        s1.assertHasPost3(results);
    }

    @Test
    fun testGetByParentPostId_noPosts() {
        val results = stagingPostIndex.getByParentId("2")
        assertThat(results).isEmpty()
    }

    @Test
    fun testPurge() {
        assertThat(stagingPostIndex.getAll()).hasSize(3)
        stagingPostIndex.purge()
        assertThat(stagingPostIndex.getAll()).isEmpty()
    }

    @Test
    fun testTrimHtmlAndMultiWhitespace(){
        assertThat(stripHtmlTagsAndMultiWhitespace("" +
                "<html>\n" +
                "    <body>\n" +
                "        <span>Hi there my name is Ben</span>\n" +
                "        <span>and I live in London.</span>\n" +
                "    </body>\n" +
                "</html>\n\n")).isEqualTo("Hi there my name is Ben and I live in London.")
    }
}