package org.tools4j.stacked.index

import org.junit.jupiter.api.Test

class PostsParseXmlTest {
    private val s1 = CoffeeStagingAssertions()

    @Test
    fun testParsePosts(){
        val posts = ArrayList<StagingPost>()
        val xmlRowParser = XmlFileParser(this.javaClass.getResourceAsStream("/data/coffee/Posts.xml"),
            PostXmlRowHandler(ToListHandler(posts)) )
        xmlRowParser.parse()

        s1.assertHasAllPosts(posts)
        s1.assertHasPost2(posts)
        s1.assertHasPost3(posts)
    }
}