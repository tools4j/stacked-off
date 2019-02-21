package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.fail

fun assertHasRawPost1(posts: List<RawPost>) {
    var post = getPostWithId(posts, "1")
    assertIsRawPost1(post)
}

fun assertHasPost1(posts: List<Post>) {
    var post = getPostWithId(posts, "1")
    assertIsPost1(post)
}

fun assertIsPost1(post: Post) {
    assertIsRawPost1(post)
    assertThat(post.comments).hasSize(5)
    assertHasComment4(post.comments)
    assertHasComment6(post.comments)
    assertHasComment7(post.comments)
    assertHasComment8(post.comments)
    assertHasComment9(post.comments)
}

fun assertIsRawPost1(post: RawPost) {
    assertThat(post.id).isEqualTo("1")
    assertThat(post.postTypeId).isEqualTo("1")
    assertThat(post.creationDate).isEqualTo("2015-01-27T21:23:05.507")
    assertThat(post.score).isEqualTo("7")
    assertThat(post.viewCount).isEqualTo("259")
    assertThat(post.body).isEqualTo("<p>While answering a few of <a href=\"https://coffee.stackexchange.com/users/8/edchum\">EdChum</a>'s questions I discovered that what I/we in the USA call pour over coffee is referred to as drip coffee in the UK. I added the pour-over tag to both questions I encountered but figured we should decide as a community which tag to use to describe this brewing process and then properly document it because drip-coffee means something different in the US (which is apparently referred to as filter-cofee in the UK). For clarification the method in question is shown in the image below. </p>\n\n<p><img src=\"https://i.stack.imgur.com/8BYnT.jpg\" alt=\"enter image description here\"> </p>\n")
    assertThat(post.ownerUserId).isEqualTo("2")
    assertThat(post.lastActivityDate).isEqualTo("2015-01-28T01:53:35.523")
    assertThat(post.tags).isEqualTo("<discussion><tags>")
    assertThat(post.parentId).isNull()
    assertThat(post.favoriteCount).isNull()
    assertThat(post.title).isEqualTo("Should we describe the process of brewing a single cup via pouring water over ground coffee as pour-over-coffee or drip-coffee?")
}

fun assertHasPost2(posts: List<Post>) {
    var post = getPostWithId(posts, "2")
    assertIsPost2(post)
}

fun assertIsPost2(post: Post) {
    assertIsRawPost2(post)
    assertThat(post.comments).hasSize(2)
    assertHasComment3(post.comments)
    assertHasComment5(post.comments)
}

fun assertHasRawPost2(posts: List<RawPost>) {
    var post = getPostWithId(posts, "2")
    assertIsRawPost2(post)
}

fun assertIsRawPost2(post: RawPost) {
    assertThat(post.id).isEqualTo("2")
    assertThat(post.postTypeId).isEqualTo("1")
    assertThat(post.creationDate).isEqualTo("2015-01-27T21:26:10.227")
    assertThat(post.score).isEqualTo("5")
    assertThat(post.viewCount).isEqualTo("49")
    assertThat(post.body).isEqualTo("<p>Being newly created brewing coffee we have zero feeds appearing in our main chat right now. What blogs, news sites, or other important coffee related things should appear in our main chat room's feed? Post your suggestions/submissions.  </p>\n")
    assertThat(post.ownerUserId).isEqualTo("2")
    assertThat(post.lastActivityDate).isEqualTo("2015-02-06T14:14:32.833")
    assertThat(post.tags).isEqualTo("<discussion>")
    assertThat(post.parentId).isNull()
    assertThat(post.favoriteCount).isEqualTo("0")
    assertThat(post.title).isEqualTo("What should go in our main chat feeds?")
}

fun assertHasRawPost3(posts: List<RawPost>) {
    var post = getPostWithId(posts, "3")
    assertIsRawPost3(post)
}

fun assertHasPost3(posts: List<Post>) {
    var post = getPostWithId(posts, "3")
    assertIsPost3(post)
}

fun assertIsPost3(post: Post) {
    assertIsRawPost3(post)
    assertThat(post.comments).hasSize(2)
    assertHasComment1(post.comments)
    assertHasComment2(post.comments)
}

fun assertIsRawPost3(post: RawPost) {
    assertThat(post.id).isEqualTo("3")
    assertThat(post.postTypeId).isEqualTo("2")
    assertThat(post.creationDate).isEqualTo("2015-01-27T21:30:20.953")
    assertThat(post.score).isEqualTo("8")
    assertThat(post.viewCount).isNull()
    assertThat(post.body).isEqualTo("<p>It looks like filter coffee has <a href=\"http://en.wikipedia.org/wiki/Indian_filter_coffee\" rel=\"nofollow\">another, different meaning</a> too. When I read \"drip coffee,\" I think of the kind you <a href=\"http://en.wikipedia.org/wiki/Drip_brew\" rel=\"nofollow\">get from a traditional coffeemaker</a>. Go for \"pour-over.\"</p>\n")
    assertThat(post.ownerUserId).isEqualTo("1")
    assertThat(post.lastActivityDate).isEqualTo("2015-01-27T21:30:20.953")
    assertThat(post.tags).isNull()
    assertThat(post.parentId).isEqualTo("1")
    assertThat(post.favoriteCount).isNull()
    assertThat(post.title).isNull()
}

fun <T: RawPost> getPostWithId(posts: List<T>, id: String): T {
    var post = posts.find { it.id == id }
    if (post == null) {
        fail("Could not find post[$id]. Posts found: ${posts.map { it.id }}")
    } else {
        return post!!
    }
}
