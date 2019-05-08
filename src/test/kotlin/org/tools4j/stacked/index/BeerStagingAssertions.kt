package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.fail

class BeerStagingAssertions(val indexedSiteId: String) {
    constructor(): this(beerSiteIndexUtils.siteId)

    /////////////////////////////////////////////////////////////////////////////
    //POSTS
    /////////////////////////////////////////////////////////////////////////////

    fun assertHasAllPosts(posts: List<StagingPost>) {
        assertHasStagingPost1(posts)
        assertHasStagingPost2(posts)
        assertHasStagingPost3(posts)
    }

    fun assertHasStagingPost1(posts: List<StagingPost>) {
        var post = getPostWithId(posts, "1")
        assertIsStagingPost1(post)
    }

    fun assertIsStagingPost1(post: StagingPost) {
        assertThat(post.id).isEqualTo("1")
        assertThat(post.creationDate).isEqualTo("2014-01-21T20:58:43.500")
        assertThat(post.score).isEqualTo("12")
        assertThat(post.viewCount).isEqualTo("145")
        assertThat(post.body).isEqualTo("<p>I've already seen a question or two that seem to at least tangentially reference Homebrewing. </p>\n\n<p>Keeping in mind that there is already a beta site on Homebrewing, how much of the topic should we allow and how much should we be prepared to migrate their direction?</p>\n")
        assertThat(post.userId).isEqualTo("1")
        assertThat(post.lastActivityDate).isEqualTo("2014-01-23T08:44:14.440")
        assertThat(post.title).isEqualTo("Is Homebrewing on topic?")
        assertThat(post.tags).isEqualTo("<discussion><scope><homebrew>")
    }

    fun assertHasStagingPost2(posts: List<StagingPost>) {
        var post = getPostWithId(posts, "2")
        assertIsStagingPost2(post)
    }

    fun assertIsStagingPost2(post: StagingPost) {
        assertThat(post.id).isEqualTo("2")
        assertThat(post.parentId).isEqualTo("1")
        assertThat(post.creationDate).isEqualTo("2014-01-21T21:05:11.577")
        assertThat(post.score).isEqualTo("21")
        assertThat(post.body).isEqualTo("<p>I would say that if there is a site for Homebrewing then technical questions about brewing beer at home are off topic.  But questions about \"homebrewed\" beer would not be off topic. </p>\n")
        assertThat(post.userId).isEqualTo("1")
        assertThat(post.lastActivityDate).isEqualTo("2014-01-21T21:05:11.577")
    }

    fun assertHasStagingPost3(posts: List<StagingPost>) {
        var post = getPostWithId(posts, "3")
        assertIsStagingPost3(post)
    }

    fun assertHasPost3(posts: List<StagingPost>) {
        var post = getPostWithId(posts, "3")
        assertIsStagingPost3(post)
    }

    fun assertIsStagingPost3(post: StagingPost) {
        assertThat(post.id).isEqualTo("3")
        assertThat(post.creationDate).isEqualTo("2014-01-21T21:06:16.967")
        assertThat(post.score).isEqualTo("7")
        assertThat(post.viewCount).isEqualTo("128")
        assertThat(post.body).isEqualTo("<p>I've seen several questions so far that blah deal with human biology as it relates directly to alcohol. </p>\n\n<p>It seems like these questions are off topic and generally outside of the expertise of this site.</p>\n\n<p>Examples:</p>\n\n<ul>\n<li><p><a href=\"https://alcohol.stackexchange.com/questions/7/will-certain-types-of-beer-get-me-more-drunk-more-quickly\">Will certain types of beer get me more drunk more quickly?</a></p></li>\n<li><p><a href=\"https://alcohol.stackexchange.com/questions/16/why-do-i-seem-to-pee-out-more-beer-than-i-drink\">Why do I seem to pee out more beer than I drink?</a></p></li>\n</ul>\n\n<p>How much of this topic can we feasibly cover, and how much of it <em>should</em> we cover?</p>\n")
        assertThat(post.userId).isEqualTo("3")
        assertThat(post.lastActivityDate).isEqualTo("2014-01-22T23:43:21.513")
        assertThat(post.title).isEqualTo("How much do we want to get into biology?")
        assertThat(post.tags).isEqualTo("<discussion><scope>")
    }

    fun <T: StagingPost> getPostWithId(posts: List<T>, id: String): T {
        var post = posts.find { it.id == id }
        if (post == null) {
            fail("Could not find post[$id]. Posts found: ${posts.map { it.id }}")
        } else {
            return post!!
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    //COMMENTS
    /////////////////////////////////////////////////////////////////////////////

    fun assertHasAllComments(comments: List<StagingComment>) {
        assertHasComment1(comments)
        assertHasComment2(comments)
        assertHasComment3(comments)
    }

    fun assertHasComment1(comments: List<StagingComment>) {
        var comment = getCommentWithId(comments, "1")
        assertThat(comment.id).isEqualTo("1")
        assertThat(comment.postId).isEqualTo("1")
        assertThat(comment.score).isEqualTo("4")
        assertThat(comment.text).isEqualTo("Not so sure, let's have a discussion: [Should Food/Beer pairings be on topic?](http://meta.beer.stackexchange.com/q/11)")
        assertThat(comment.creationDate).isEqualTo("2014-01-21T21:53:17.400")
        assertThat(comment.userId).isEqualTo("1")
    }

    fun assertHasComment2(comments: List<StagingComment>) {
        var comment = getCommentWithId(comments, "2")
        assertThat(comment.id).isEqualTo("2")
        assertThat(comment.postId).isEqualTo("1")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("possible duplicate of [Proposal: \\[pairing\\] is a bad tag and should be removed](http://meta.beer.stackexchange.com/questions/10/proposal-pairing-is-a-bad-tag-and-should-be-removed)")
        assertThat(comment.creationDate).isEqualTo("2014-01-21T21:54:57.053")
        assertThat(comment.userId).isEqualTo("2")
    }

    fun assertHasComment3(comments: List<StagingComment>) {
        var comment = getCommentWithId(comments, "3")
        assertThat(comment.id).isEqualTo("3")
        assertThat(comment.postId).isEqualTo("2")
        assertThat(comment.score).isEqualTo("3")
        assertThat(comment.text).isEqualTo("Not a duplicate, although the outcome of that discussion *should* hinge on this one.")
        assertThat(comment.creationDate).isEqualTo("2014-01-21T22:01:41.543")
        assertThat(comment.userId).isEqualTo("3")
    }

    private fun getCommentWithId(comments: List<StagingComment>, id: String): StagingComment {
        var comment = comments.find { it.id == id }
        if (comment == null) {
            fail("Could not find post[$id]. Posts found: ${comments.map { it.id }}")
        } else {
            return comment!!
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    //USERS
    /////////////////////////////////////////////////////////////////////////////

    fun assertHasAllUsers(users: List<StagingUser>) {
        assertHasUser1(users)
        assertHasUser2(users)
        assertHasUser3(users)
    }

    fun assertHasUser1(users: List<StagingUser>) {
        assertUser1(getUserWithId(users, "1"))
    }

    fun assertUser1(user: StagingUser) {
        assertThat(user.id).isEqualTo("1")
        assertThat(user.reputation).isEqualTo("101")
        assertThat(user.displayName).isEqualTo("Geoff Dalgas")
        assertThat(user.accountId).isEqualTo("2")
    }

    fun assertHasUser2(users: List<StagingUser>) {
        assertUser2(getUserWithId(users, "2"))
    }

    fun assertUser2(user: StagingUser) {
        assertThat(user.id).isEqualTo("2")
        assertThat(user.reputation).isEqualTo("268")
        assertThat(user.displayName).isEqualTo("Kasra Rahjerdi")
        assertThat(user.accountId).isEqualTo("109934")
    }

    fun assertHasUser3(users: List<StagingUser>) {
        assertUser3(getUserWithId(users, "3"))
    }

    fun assertUser3(user: StagingUser) {
        assertThat(user.id).isEqualTo("3")
        assertThat(user.reputation).isEqualTo("99")
        assertThat(user.displayName).isEqualTo("Adam Lear")
        assertThat(user.accountId).isEqualTo("37099")
    }

    private fun getUserWithId(users: List<StagingUser>, id: String): StagingUser {
        var user = users.find { it.id == id }
        if (user == null) {
            fail("Could not find post[$id]. Posts found: ${users.map { it.id }}")
        } else {
            return user!!
        }
    }
}