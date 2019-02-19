package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PostIndexTest {
    @Test
    fun testQueryAllPostsFromIndex() {
        val postIndex = PostIndex(RamIndexFactory())
        postIndex.init()

        val postXmlRowHandler = PostXmlRowHandler(postIndex.getItemHandler())
        val xmlFileParser = XmlFileParser("/data/example/Posts.xml", XmlRowHandlerFactory(listOf(postXmlRowHandler)))
        xmlFileParser.parse()
        val results = postIndex.query("coffee")
        
        assertThat(results).hasSize(3)
        PostTestUtils.assertHasPostOne(results);
        PostTestUtils.assertHasPostTwo(results);
        PostTestUtils.assertHasPostThree(results);
    }
}