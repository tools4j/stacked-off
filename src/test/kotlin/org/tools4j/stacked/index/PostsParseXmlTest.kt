package org.tools4j.stacked.index

import org.junit.jupiter.api.Test

class PostsParseXmlTest {
    private val s1 = Site1Assertions()

    @Test
    fun testParsePosts(){
        val posts = ArrayList<RawPost>()
        val xmlRowHandlerFactory = XmlRowHandlerFactory(listOf(PostXmlRowHandler(ToListHandler(posts))))
        val xmlRowParser = XmlFileParser("/data/example/Posts.xml", "1", xmlRowHandlerFactory)
        xmlRowParser.parse()

        s1.assertHasRawPost1(posts)
        s1.assertHasRawPost2(posts)
        s1.assertHasRawPost3(posts)
    }
}