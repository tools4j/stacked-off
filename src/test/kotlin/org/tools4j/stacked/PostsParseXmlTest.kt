package org.tools4j.stacked

import org.junit.jupiter.api.Test

class PostsParseXmlTest {
    @Test
    fun testParsePosts(){
        val posts = ArrayList<RawPost>()
        val xmlRowHandlerFactory = XmlRowHandlerFactory(listOf(PostXmlRowHandler(ToListHandler(posts))))
        val xmlRowParser = XmlFileParser("/data/example/Posts.xml", xmlRowHandlerFactory)
        xmlRowParser.parse()

        assertHasRawPost1(posts)
        assertHasRawPost2(posts)
        assertHasRawPost3(posts)
    }
}