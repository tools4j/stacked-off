package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.fail

class CoffeeStagingAssertions(val indexedSiteId: String) {
    constructor(): this(coffeeSiteIndexUtils.siteId)

    /////////////////////////////////////////////////////////////////////////////
    //POSTS
    /////////////////////////////////////////////////////////////////////////////

    fun assertHasAllPosts(posts: List<StagingPost>) {
        assertHasPost1(posts)
        assertHasPost2(posts)
        this.assertHasPost3(posts)
    }

    fun assertHasPost1(posts: List<StagingPost>) {
        var post = getPostWithId(posts, "1")
        assertIsPost1(post)
    }

    fun assertIsPost1(post: StagingPost) {
        assertThat(post.id).isEqualTo("1")
        assertThat(post.creationDate).isEqualTo("2015-01-27T21:23:05.507")
        assertThat(post.score).isEqualTo("7")
        assertThat(post.viewCount).isEqualTo("259")
        assertThat(post.body).isEqualTo("<p>While answering a few of <a href=\"https://coffee.stackexchange.com/users/8/edchum\">EdChum</a>'s questions I discovered that what I/we in the USA call pour over coffee is referred to as drip coffee in the UK. I added the pour-over tag to both questions I encountered but figured we should decide as a community which tag to use to describe this brewing process and then properly document it because drip-coffee means something different in the US (which is apparently referred to as filter-cofee in the UK). For clarification the method in question is shown in the image below. </p>\n\n<p><img src=\"https://i.stack.imgur.com/8BYnT.jpg\" alt=\"enter image description here\"> </p>\n")
        assertThat(post.userId).isEqualTo("2")
        assertThat(post.lastActivityDate).isEqualTo("2015-01-28T01:53:35.523")
        assertThat(post.tags).isEqualTo("<discussion><tags>")
        assertThat(post.parentId).isNull()
        assertThat(post.favoriteCount).isNull()
        assertThat(post.title).isEqualTo("Should we describe the process of brewing a single cup via pouring water over ground coffee as pour-over-coffee or drip-coffee?")
    }

    fun assertHasPost2(posts: List<StagingPost>) {
        var post = getPostWithId(posts, "2")
        assertIsPost2(post)
    }

    fun assertIsPost2(post: StagingPost) {
        assertThat(post.id).isEqualTo("2")
        assertThat(post.creationDate).isEqualTo("2015-01-27T21:26:10.227")
        assertThat(post.score).isEqualTo("5")
        assertThat(post.viewCount).isEqualTo("49")
        assertThat(post.body).isEqualTo("<p>Being newly created brewing coffee we have zero feeds appearing in our main chat right now. What blogs, news sites, or other important coffee related things should appear in our main chat room's feed? Post your suggestions/submissions.  </p>\n")
        assertThat(post.userId).isEqualTo("2")
        assertThat(post.lastActivityDate).isEqualTo("2015-02-06T14:14:32.833")
        assertThat(post.tags).isEqualTo("<discussion>")
        assertThat(post.parentId).isNull()
        assertThat(post.favoriteCount).isEqualTo("0")
        assertThat(post.title).isEqualTo("What should go in our main chat feeds?")
    }

    fun assertHasPost3(posts: List<StagingPost>) {
        var post = getPostWithId(posts, "3")
        assertIsPost3(post)
    }

    fun assertIsPost3(post: StagingPost) {
        assertThat(post.id).isEqualTo("3")
        assertThat(post.creationDate).isEqualTo("2015-01-27T21:30:20.953")
        assertThat(post.score).isEqualTo("8")
        assertThat(post.viewCount).isNull()
        assertThat(post.body).isEqualTo("<p>It looks like blah filter coffee has <a href=\"http://en.wikipedia.org/wiki/Indian_filter_coffee\" rel=\"nofollow\">another, different meaning</a> too. When I read \"drip coffee,\" I think of the kind you <a href=\"http://en.wikipedia.org/wiki/Drip_brew\" rel=\"nofollow\">get from a traditional coffeemaker</a>. Go for \"pour-over.\"</p>\n")
        assertThat(post.userId).isEqualTo("1")
        assertThat(post.lastActivityDate).isEqualTo("2015-01-27T21:30:20.953")
        assertThat(post.tags).isNull()
        assertThat(post.parentId).isEqualTo("1")
        assertThat(post.favoriteCount).isNull()
        assertThat(post.title).isNull()
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
        assertHasComment4(comments)
        assertHasComment6(comments)
        assertHasComment7(comments)
        assertHasComment8(comments)
        assertHasComment9(comments)
    }

    fun assertHasComment1(comments: List<StagingComment>) {
        var comment = getCommentWithId(comments, "1")
        assertThat(comment.id).isEqualTo("1")
        assertThat(comment.postId).isEqualTo("3")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("*Wave* hello fellow rpg.se user.")
        assertThat(comment.creationDate).isEqualTo("2015-01-27T21:31:29.540")
        assertThat(comment.userId).isEqualTo("2")
    }

    fun assertHasComment2(comments: List<StagingComment>) {
        var comment = getCommentWithId(comments, "2")
        assertThat(comment.id).isEqualTo("2")
        assertThat(comment.postId).isEqualTo("3")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("@JoshuaAslanSmith: Cheers. :)")
        assertThat(comment.creationDate).isEqualTo("2015-01-27T22:17:51.780")
        assertThat(comment.userId).isEqualTo("1")
    }

    fun assertHasComment3(comments: List<StagingComment>) {
        var comment = getCommentWithId(comments, "3")
        assertThat(comment.id).isEqualTo("3")
        assertThat(comment.postId).isEqualTo("2")
        assertThat(comment.score).isEqualTo("2")
        assertThat(comment.text).isEqualTo("I've gone ahead and added a feed for this site (meta.coffee) already, since meta's a very important part of the community.")
        assertThat(comment.creationDate).isEqualTo("2015-01-27T22:33:57.407")
        assertThat(comment.userId).isEqualTo("3")
    }

    fun assertHasComment4(comments: List<StagingComment>) {
        var comment = getCommentWithId(comments, "4")
        assertThat(comment.id).isEqualTo("4")
        assertThat(comment.postId).isEqualTo("1")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("oh, these are terms for _that_? never heard either… maybe allow both and recommend elaboration in the text?")
        assertThat(comment.creationDate).isEqualTo("2015-01-27T22:55:08.750")
        assertThat(comment.userId).isEqualTo("4")
    }

    fun assertHasComment5(comments: List<StagingComment>) {
        var comment = getCommentWithId(comments, "5")
        assertThat(comment.id).isEqualTo("5")
        assertThat(comment.postId).isEqualTo("2")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("@Doorknob add that as an answer so I can upvote (for the meta rep as much as that mattters lol)\\")
        assertThat(comment.creationDate).isEqualTo("2015-01-27T22:56:19.410")
        assertThat(comment.userId).isEqualTo("2")
    }

    fun assertHasComment6(comments: List<StagingComment>) {
        var comment = getCommentWithId(comments, "6")
        assertThat(comment.id).isEqualTo("6")
        assertThat(comment.postId).isEqualTo("1")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("Recommending both would be confusing; generally you want to use one tag, and make any other names a tag synonym (you don't want to have half the questions have one name, and the other have a different one, making it hard to get a list of all questions about that particular topic).")
        assertThat(comment.creationDate).isEqualTo("2015-01-28T01:48:22.847")
        assertThat(comment.userId).isEqualTo("5")
    }

    fun assertHasComment7(comments: List<StagingComment>) {
        var comment = getCommentWithId(comments, "7")
        assertThat(comment.id).isEqualTo("7")
        assertThat(comment.postId).isEqualTo("1")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("Certainly, the terms \"pour-over\", \"drip\", and \"filter\" (and perhaps others...) need regional disambiguation; perhaps this should be a question at [main] main site? I fear clarifying text in every question/answer will be necessary. Making uniform the vernacular of the entire world might be easier. ;-)")
        assertThat(comment.creationDate).isEqualTo("2015-02-11T16:32:01.823")
        assertThat(comment.userId).isEqualTo("6")
    }

    fun assertHasComment8(comments: List<StagingComment>) {
        var comment = getCommentWithId(comments, "8")
        assertThat(comment.id).isEqualTo("8")
        assertThat(comment.postId).isEqualTo("1")
        assertThat(comment.score).isEqualTo("1")
        assertThat(comment.text).isEqualTo("@hoc_age Tagging terminology needs to be nailed down in the tag wiki for the site so for our purposes Yes, it does need to be agreed upon and defined. Meta is the place to talk about and discuss issues on the main site.")
        assertThat(comment.creationDate).isEqualTo("2015-02-11T17:02:35.463")
        assertThat(comment.userId).isEqualTo("2")
    }

    fun assertHasComment9(comments: List<StagingComment>) {
        var comment = getCommentWithId(comments, "9")
        assertThat(comment.id).isEqualTo("9")
        assertThat(comment.postId).isEqualTo("1")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("@JoshuaAslanSmith - agreed about the tag terminology, and I think  you've convinced me that the best place to \"define\" our *lingua franca* is the tag wiki. My comment on [this answer](http://meta.coffee.stackexchange.com/a/24/262) summarizes my feelings about the (more-)general problem.")
        assertThat(comment.creationDate).isEqualTo("2015-02-11T17:09:52.737")
        assertThat(comment.userId).isEqualTo("6")
    }

    private fun getCommentWithId(comments: List<StagingComment>, id: String): StagingComment {
        var comment = comments.find { it.id == id }
        if (comment == null) {
            fail("Could not find comment[$id]. Comments found: ${comments.map { it.id }}")
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
        assertHasUser4(users)
        assertHasUser5(users)
        assertHasUser6(users)
    }

    fun assertHasUser1(users: List<StagingUser>) {
        assertUser1(getUserWithId(users, "1"))
    }

    fun assertUser1(user: StagingUser) {
        assertThat(user.id).isEqualTo("1")
        assertThat(user.reputation).isEqualTo("103")
        assertThat(user.displayName).isEqualTo("Jadasc")
        assertThat(user.accountId).isEqualTo("508203")
    }

    fun assertHasUser2(users: List<StagingUser>) {
        assertUser2(getUserWithId(users, "2"))
    }

    fun assertUser2(user: StagingUser) {
        assertThat(user.id).isEqualTo("2")
        assertThat(user.reputation).isEqualTo("1241")
        assertThat(user.displayName).isEqualTo("Joshua Aslan Smith")
        assertThat(user.accountId).isEqualTo("1454870")
    }

    fun assertHasUser3(users: List<StagingUser>) {
        assertUser3(getUserWithId(users, "3"))
    }

    fun assertUser3(user: StagingUser) {
        assertThat(user.id).isEqualTo("3")
        assertThat(user.reputation).isEqualTo("101")
        assertThat(user.displayName).isEqualTo("Doorknob")
        assertThat(user.accountId).isEqualTo("1266491")
    }

    fun assertHasUser4(users: List<StagingUser>) {
        assertUser4(getUserWithId(users, "4"))
    }

    fun assertUser4(user: StagingUser) {
        assertThat(user.id).isEqualTo("4")
        assertThat(user.reputation).isEqualTo("101")
        assertThat(user.displayName).isEqualTo("mirabilos")
        assertThat(user.accountId).isEqualTo("2494278")
    }

    fun assertHasUser5(users: List<StagingUser>) {
        assertUser5(getUserWithId(users, "5"))
    }

    fun assertUser5(user: StagingUser) {
        assertThat(user.id).isEqualTo("5")
        assertThat(user.reputation).isEqualTo("488")
        assertThat(user.displayName).isEqualTo("Sam Whited")
        assertThat(user.accountId).isEqualTo("141416")
    }

    fun assertHasUser6(users: List<StagingUser>) {
        assertUser6(getUserWithId(users, "6"))
    }

    fun assertUser6(user: StagingUser) {
        assertThat(user.id).isEqualTo("6")
        assertThat(user.reputation).isEqualTo("6844")
        assertThat(user.displayName).isEqualTo("hoc_age")
        assertThat(user.accountId).isEqualTo("4366363")
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