package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PostsParseXmlTest {
    @Test
    fun testParsePosts(){
        val posts = Posts.fromXmlOnClasspath("/data/example/Posts.xml").posts!!
        assertThat(posts).hasSize(3)

        PostTestUtils.assertHasPostOne(posts)
        PostTestUtils.assertHasPostTwo(posts)
        PostTestUtils.assertHasPostThree(posts)
    }
}