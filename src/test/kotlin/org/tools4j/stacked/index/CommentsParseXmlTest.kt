package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CommentsParseXmlTest {
    private val s1 = Site1Assertions()

    @Test
    fun testParseComments(){
        val comments = ArrayList<RawComment>()
        val xmlRowHandlerFactory = XmlRowHandlerFactory(listOf(CommentXmlRowHandler(ToListHandler(comments))))
        val xmlFileParser = XmlFileParser("/data/example/Comments.xml", "1", xmlRowHandlerFactory)
        xmlFileParser.parse()

        assertThat(comments).hasSize(9)
        assertThat(s1.assertHasAllComments(comments))
    }
}