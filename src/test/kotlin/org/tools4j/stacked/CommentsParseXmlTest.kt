package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CommentsParseXmlTest {
    @Test
    fun testParseComments(){
        val comments = ArrayList<RawComment>()
        val xmlRowHandlerFactory = XmlRowHandlerFactory(listOf(CommentXmlRowHandler(ToListHandler(comments))))
        val xmlFileParser = XmlFileParser("/data/example/Comments.xml", xmlRowHandlerFactory)
        xmlFileParser.parse()

        assertThat(comments).hasSize(9)
        assertThat(assertHasAllComments(comments))
    }
}