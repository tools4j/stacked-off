package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CommentsParseXmlTest {
    private val s1 = CoffeeStagingAssertions()

    @Test
    fun testParseComments(){
        val comments = ArrayList<StagingComment>()
        val xmlFileParser = XmlFileParser(this.javaClass.getResourceAsStream("/data/coffee/Comments.xml"),
            CommentXmlRowHandler(ToListHandler(comments)))
        xmlFileParser.parse()

        assertThat(comments).hasSize(9)
        assertThat(s1.assertHasAllComments(comments))
    }
}