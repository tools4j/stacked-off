package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.fail

class BeerSiteAssertions(val indexedSiteId: String) {
    constructor(): this(SITE_2)

    /////////////////////////////////////////////////////////////////////////////
    //QUESTIONS
    /////////////////////////////////////////////////////////////////////////////

    fun assertHasQuestion1(questions: Collection<Question>){
        val question = getQuestionByUid("$indexedSiteId.1", questions)
        assertThat(question).isNotNull
        assertIsQuestion1(question!!)
    }

    fun assertHasQuestion3(questions: Collection<Question>){
        val question = getQuestionByUid("$indexedSiteId.3", questions)
        assertThat(question).isNotNull
        assertIsQuestion3(question!!)
    }

    fun getQuestionByUid(uid: String, questions: Collection<Question>): Question? {
        return questions.firstOrNull { it.uid == uid }
    }

    fun assertIsQuestion1(question: Question){
        assertThat(question.indexedSite.indexedSiteId).isEqualTo(indexedSiteId)
        assertIsPost1(question)
        assertThat(question.childPosts).hasSize(1)
        assertHasPost2(question.childPosts)
    }

    fun assertIsQuestion3(question: Question){
        assertThat(question.indexedSite.indexedSiteId).isEqualTo(indexedSiteId)
        assertIsPost3(question)
        assertThat(question.childPosts).isEmpty()
    }

    /////////////////////////////////////////////////////////////////////////////
    //POSTS
    /////////////////////////////////////////////////////////////////////////////

    fun assertHasAllRawPosts(posts: List<RawPost>) {
        assertHasRawPost1(posts)
        assertHasRawPost2(posts)
        assertHasRawPost3(posts)
    }

    fun assertHasRawPost1(posts: List<RawPost>) {
        var post = getPostWithUid(posts, "$indexedSiteId.1")
        assertIsRawPost1(post)
    }

    fun assertHasPost1(posts: List<Post>) {
        var post = getPostWithUid(posts, "$indexedSiteId.1")
        assertIsPost1(post)
    }

    fun assertIsPost1(post: Post) {
        assertIsRawPost1(post)
        assertUser1(post.ownerUser!!)
        assertThat(post.comments).hasSize(2)
        assertHasComment1(post.comments)
        assertHasComment2(post.comments)
    }

    fun assertIsRawPost1(post: RawPost) {
        assertThat(post.uid).isEqualTo("$indexedSiteId.1")
        assertThat(post.postTypeId).isEqualTo("1")
        assertThat(post.creationDate).isEqualTo("2014-01-21T20:58:43.500")
        assertThat(post.score).isEqualTo("12")
        assertThat(post.viewCount).isEqualTo("145")
        assertThat(post.body).isEqualTo("<p>I've already seen a question or two that seem to at least tangentially reference Homebrewing. </p>\n\n<p>Keeping in mind that there is already a beta site on Homebrewing, how much of the topic should we allow and how much should we be prepared to migrate their direction?</p>\n")
        assertThat(post.ownerUserUid).isEqualTo("$indexedSiteId.1")
        assertThat(post.lastActivityDate).isEqualTo("2014-01-23T08:44:14.440")
        assertThat(post.title).isEqualTo("Is Homebrewing on topic?")
        assertThat(post.tags).isEqualTo("<discussion><scope><homebrew>")
    }

    fun assertHasPost2(posts: List<Post>) {
        var post = getPostWithUid(posts, "$indexedSiteId.2")
        assertIsPost2(post)
    }

    fun assertIsPost2(post: Post) {
        assertIsRawPost2(post)
        assertUser1(post.ownerUser!!)
        assertThat(post.comments).hasSize(1)
        assertHasComment3(post.comments)
    }

    fun assertHasRawPost2(posts: List<RawPost>) {
        var post = getPostWithUid(posts, "$indexedSiteId.2")
        assertIsRawPost2(post)
    }

    fun assertIsRawPost2(post: RawPost) {
        assertThat(post.uid).isEqualTo("$indexedSiteId.2")
        assertThat(post.postTypeId).isEqualTo("2")
        assertThat(post.parentUid).isEqualTo("$indexedSiteId.1")
        assertThat(post.creationDate).isEqualTo("2014-01-21T21:05:11.577")
        assertThat(post.score).isEqualTo("21")
        assertThat(post.body).isEqualTo("<p>I would say that if there is a site for Homebrewing then technical questions about brewing beer at home are off topic.  But questions about \"homebrewed\" beer would not be off topic. </p>\n")
        assertThat(post.ownerUserUid).isEqualTo("$indexedSiteId.1")
        assertThat(post.lastActivityDate).isEqualTo("2014-01-21T21:05:11.577")
    }

    fun assertHasRawPost3(posts: List<RawPost>) {
        var post = getPostWithUid(posts, "$indexedSiteId.3")
        assertIsRawPost3(post)
    }

    fun assertHasPost3(posts: List<Post>) {
        var post = getPostWithUid(posts, "$indexedSiteId.3")
        assertIsPost3(post)
    }

    fun assertIsPost3(post: Post) {
        assertIsRawPost3(post)
        assertUser3(post.ownerUser!!)
        assertThat(post.comments).isEmpty()
    }

    fun assertIsRawPost3(post: RawPost) {
        assertThat(post.uid).isEqualTo("$indexedSiteId.3")
        assertThat(post.postTypeId).isEqualTo("1")
        assertThat(post.creationDate).isEqualTo("2014-01-21T21:06:16.967")
        assertThat(post.score).isEqualTo("7")
        assertThat(post.viewCount).isEqualTo("128")
        assertThat(post.body).isEqualTo("<p>I've seen several questions so far that blah deal with human biology as it relates directly to alcohol. </p>\n\n<p>It seems like these questions are off topic and generally outside of the expertise of this site.</p>\n\n<p>Examples:</p>\n\n<ul>\n<li><p><a href=\"https://alcohol.stackexchange.com/questions/7/will-certain-types-of-beer-get-me-more-drunk-more-quickly\">Will certain types of beer get me more drunk more quickly?</a></p></li>\n<li><p><a href=\"https://alcohol.stackexchange.com/questions/16/why-do-i-seem-to-pee-out-more-beer-than-i-drink\">Why do I seem to pee out more beer than I drink?</a></p></li>\n</ul>\n\n<p>How much of this topic can we feasibly cover, and how much of it <em>should</em> we cover?</p>\n")
        assertThat(post.ownerUserUid).isEqualTo("$indexedSiteId.3")
        assertThat(post.lastActivityDate).isEqualTo("2014-01-22T23:43:21.513")
        assertThat(post.title).isEqualTo("How much do we want to get into biology?")
        assertThat(post.tags).isEqualTo("<discussion><scope>")
    }

    fun <T: RawPost> getPostWithUid(posts: List<T>, uid: String): T {
        var post = posts.find { it.uid == uid }
        if (post == null) {
            fail("Could not find post[$uid]. Posts found: ${posts.map { it.uid }}")
        } else {
            return post!!
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    //COMMENTS
    /////////////////////////////////////////////////////////////////////////////

    fun assertHasAllComments(rawComments: List<RawComment>) {
        assertHasComment1(rawComments)
        assertHasComment2(rawComments)
        assertHasComment3(rawComments)
    }

    fun assertHasComment1(rawComments: List<RawComment>) {
        var comment = getCommentWithUid(rawComments, "$indexedSiteId.1")
        assertThat(comment.uid).isEqualTo("$indexedSiteId.1")
        assertThat(comment.postUid).isEqualTo("$indexedSiteId.1")
        assertThat(comment.score).isEqualTo("4")
        assertThat(comment.text).isEqualTo("Not so sure, let's have a discussion: [Should Food/Beer pairings be on topic?](http://meta.beer.stackexchange.com/q/11)")
        assertThat(comment.creationDate).isEqualTo("2014-01-21T21:53:17.400")
        assertThat(comment.userUid).isEqualTo("$indexedSiteId.1")
    }

    fun assertHasComment2(rawComments: List<RawComment>) {
        var comment = getCommentWithUid(rawComments, "$indexedSiteId.2")
        assertThat(comment.uid).isEqualTo("$indexedSiteId.2")
        assertThat(comment.postUid).isEqualTo("$indexedSiteId.1")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("possible duplicate of [Proposal: \\[pairing\\] is a bad tag and should be removed](http://meta.beer.stackexchange.com/questions/10/proposal-pairing-is-a-bad-tag-and-should-be-removed)")
        assertThat(comment.creationDate).isEqualTo("2014-01-21T21:54:57.053")
        assertThat(comment.userUid).isEqualTo("$indexedSiteId.2")
    }

    fun assertHasComment3(rawComments: List<RawComment>) {
        var comment = getCommentWithUid(rawComments, "$indexedSiteId.3")
        assertThat(comment.uid).isEqualTo("$indexedSiteId.3")
        assertThat(comment.postUid).isEqualTo("$indexedSiteId.2")
        assertThat(comment.score).isEqualTo("3")
        assertThat(comment.text).isEqualTo("Not a duplicate, although the outcome of that discussion *should* hinge on this one.")
        assertThat(comment.creationDate).isEqualTo("2014-01-21T22:01:41.543")
        assertThat(comment.userUid).isEqualTo("$indexedSiteId.3")
    }

    private fun getCommentWithUid(rawComments: List<RawComment>, uid: String): RawComment {
        var comment = rawComments.find { it.uid == uid }
        if (comment == null) {
            fail("Could not find post[$uid]. Posts found: ${rawComments.map { it.uid }}")
        } else {
            return comment!!
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    //USERS
    /////////////////////////////////////////////////////////////////////////////

    fun assertHasAllUsers(users: List<User>) {
        assertHasUser1(users)
        assertHasUser2(users)
        assertHasUser3(users)
    }

    fun assertHasUser1(users: List<User>) {
        assertUser1(getUserWithUid(users, "$indexedSiteId.1"))
    }

    fun assertUser1(user: User) {
        assertThat(user.uid).isEqualTo("$indexedSiteId.1")
        assertThat(user.reputation).isEqualTo("101")
        assertThat(user.displayName).isEqualTo("Geoff Dalgas")
        assertThat(user.accountId).isEqualTo("2")
    }

    fun assertHasUser2(users: List<User>) {
        assertUser2(getUserWithUid(users, "$indexedSiteId.2"))
    }

    fun assertUser2(user: User) {
        assertThat(user.uid).isEqualTo("$indexedSiteId.2")
        assertThat(user.reputation).isEqualTo("268")
        assertThat(user.displayName).isEqualTo("Kasra Rahjerdi")
        assertThat(user.accountId).isEqualTo("109934")
    }

    fun assertHasUser3(users: List<User>) {
        assertUser3(getUserWithUid(users, "$indexedSiteId.3"))
    }

    fun assertUser3(user: User) {
        assertThat(user.uid).isEqualTo("$indexedSiteId.3")
        assertThat(user.reputation).isEqualTo("99")
        assertThat(user.displayName).isEqualTo("Adam Lear")
        assertThat(user.accountId).isEqualTo("37099")
    }


    private fun getUserWithUid(users: List<User>, uid: String): User {
        var user = users.find { it.uid == uid }
        if (user == null) {
            fail("Could not find post[$uid]. Posts found: ${users.map { it.uid }}")
        } else {
            return user!!
        }
    }
}