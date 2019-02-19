package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.function.Consumer

internal class CommentIndexTest {
    @Test
    fun testQueryAllCommentsFromIndex() {
        val commentIndex = CommentIndex(RamIndexFactory())
        commentIndex.init()
        val commentXmlRowHandler = CommentXmlRowHandler(commentIndex.getItemHandler())
        val xmlFileParser = XmlFileParser("/data/example/Comments.xml", XmlRowHandlerFactory(listOf(commentXmlRowHandler)))
        xmlFileParser.parse()
        
        val results = commentIndex.query("question")
        
        assertThat(results).hasSize(1)
        CommentTestUtils.assertHasComment7(results);
    }
}