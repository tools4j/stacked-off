package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CommentIndexTest {
    @Test
    fun testQueryAllCommentsFromIndex() {
        val commentIndex = CommentIndex(RamIndexFactory())
        commentIndex.init()
        val comments = Comments.fromXmlOnClasspath("/data/example/Comments.xml")
        commentIndex.addComments(comments.comments!!)
        val results = commentIndex.query("question")
        
        assertThat(results).hasSize(1)
        CommentTestUtils.assertHasComment7(results);
    }
}