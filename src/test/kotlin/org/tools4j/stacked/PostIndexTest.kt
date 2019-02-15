package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PostIndexTest {
    @Test
    fun testQueryAllPostsFromIndex() {
        val postIndex = PostIndex(RamIndexFactory())
        postIndex.init()
        val posts = Posts.fromXmlOnClasspath("/data/example/Posts.xml")
        postIndex.addPosts(posts.posts!!)
        val results = postIndex.query("coffee")
        
        assertThat(results).hasSize(3)
        PostTestUtils.assertHasPostOne(results);
        PostTestUtils.assertHasPostTwo(results);
        PostTestUtils.assertHasPostThree(results);
    }
}