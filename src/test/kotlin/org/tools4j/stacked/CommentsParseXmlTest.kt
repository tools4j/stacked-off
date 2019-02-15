package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CommentsParseXmlTest {
    @Test
    fun testParseComments(){
        val comments = Comments.fromXmlOnClasspath("/data/example/Comments.xml").comments!!
        assertThat(comments).hasSize(9)
        assertThat(CommentTestUtils.assertHasAllComments(comments))
    }
}