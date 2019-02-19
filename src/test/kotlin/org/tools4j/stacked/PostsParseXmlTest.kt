package org.tools4j.stacked

import org.junit.jupiter.api.Test
import java.util.function.Consumer

class PostsParseXmlTest {
    @Test
    fun testParsePosts(){
        val posts = ArrayList<RawPost>()
        val xmlRowHandlerFactory = XmlRowHandlerFactory(listOf(PostXmlRowHandler(ToListHandler(posts))))
        val xmlRowParser = XmlFileParser("/data/example/Posts.xml", xmlRowHandlerFactory)
        xmlRowParser.parse()

        PostTestUtils.assertHasPostOne(posts)
        PostTestUtils.assertHasPostTwo(posts)
        PostTestUtils.assertHasPostThree(posts)
    }
}