package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.fail

class BeerSiteAssertions(val indexedSiteId: String) {
    constructor(): this(beerSiteIndexUtils.siteId)

    /////////////////////////////////////////////////////////////////////////////
    //QUESTIONS
    /////////////////////////////////////////////////////////////////////////////

    fun assertHasAllQuestions(questions: List<Question>) {
        assertHasQuestion1(questions)
        assertHasQuestion3(questions)
    }

    fun assertHasQuestion1(questions: Collection<Question>){
        val question = getQuestionByUid("p$indexedSiteId.1", questions)
        assertThat(question).isNotNull
        assertIsQuestion1(question!!)
    }

    fun assertIsQuestion1(question: Question) {
        assertThat(question.uid).isEqualTo("p$indexedSiteId.1")
        assertThat(question.creationDate).isEqualTo("2014-01-21T20:58:43.500")
        assertThat(question.score).isEqualTo("12")
        assertThat(question.viewCount).isEqualTo("145")
        assertThat(question.htmlContent).isEqualTo("<p>I've already seen a question or two that seem to at least tangentially reference Homebrewing. </p>\n\n<p>Keeping in mind that there is already a beta site on Homebrewing, how much of the topic should we allow and how much should we be prepared to migrate their direction?</p>\n")
        assertThat(question.userUid).isEqualTo("u$indexedSiteId.1")
        assertThat(question.lastActivityDate).isEqualTo("2014-01-23T08:44:14.440")
        assertThat(question.title).isEqualTo("Is Homebrewing on topic?")
        assertThat(question.tags).isEqualTo("<discussion><scope><homebrew>")
        assertHasUser1Fields(question)
        assertThat(question.comments).hasSize(2)
        assertHasComment1(question.comments)
        assertHasComment2(question.comments)
        assertThat(question.answers).hasSize(1)
        assertHasAnswer2(question.answers)
    }

    fun assertHasQuestion3(questions: Collection<Question>){
        val question = getQuestionByUid("p$indexedSiteId.3", questions)
        assertThat(question).isNotNull
        this.assertIsQuestion3(question!!)
    }

    fun assertIsQuestion3(question: Question) {
        assertThat(question.uid).isEqualTo("p$indexedSiteId.3")
        assertThat(question.indexedSite.indexedSiteId).isEqualTo(indexedSiteId)
        assertThat(question.creationDate).isEqualTo("2014-01-21T21:06:16.967")
        assertThat(question.score).isEqualTo("7")
        assertThat(question.viewCount).isEqualTo("128")
        assertThat(question.htmlContent).isEqualTo("<p>I've seen several questions so far that blah deal with human biology as it relates directly to alcohol. </p>\n\n<p>It seems like these questions are off topic and generally outside of the expertise of this site.</p>\n\n<p>Examples:</p>\n\n<ul>\n<li><p><a href=\"https://alcohol.stackexchange.com/questions/7/will-certain-types-of-beer-get-me-more-drunk-more-quickly\">Will certain types of beer get me more drunk more quickly?</a></p></li>\n<li><p><a href=\"https://alcohol.stackexchange.com/questions/16/why-do-i-seem-to-pee-out-more-beer-than-i-drink\">Why do I seem to pee out more beer than I drink?</a></p></li>\n</ul>\n\n<p>How much of this topic can we feasibly cover, and how much of it <em>should</em> we cover?</p>\n")
        assertThat(question.userUid).isEqualTo("u$indexedSiteId.3")
        assertThat(question.lastActivityDate).isEqualTo("2014-01-22T23:43:21.513")
        assertThat(question.title).isEqualTo("How much do we want to get into biology?")
        assertThat(question.tags).isEqualTo("<discussion><scope>")
        assertHasUser3Fields(question)
        assertThat(question.comments).isEmpty()
        assertThat(question.answers).isEmpty()
    }

    fun getQuestionByUid(uid: String, questions: Collection<Question>): Question? {
        return questions.firstOrNull { it.uid == uid }
    }
    

    /////////////////////////////////////////////////////////////////////////////
    //ANSWERS
    /////////////////////////////////////////////////////////////////////////////

    fun assertHasAnswer2(posts: List<Answer>) {
        var answer = getAnswerWithUid(posts, "p$indexedSiteId.2")
        assertIsAnswer2(answer)
    }

    fun assertIsAnswer2(post: Answer) {
        assertThat(post.uid).isEqualTo("p$indexedSiteId.2")
        assertThat(post.parentUid).isEqualTo("p$indexedSiteId.1")
        assertThat(post.creationDate).isEqualTo("2014-01-21T21:05:11.577")
        assertThat(post.score).isEqualTo("21")
        assertThat(post.htmlContent).isEqualTo("<p>I would say that if there is a site for Homebrewing then technical questions about brewing beer at home are off topic.  But questions about \"homebrewed\" beer would not be off topic. </p>\n")
        assertThat(post.userUid).isEqualTo("u$indexedSiteId.1")
        assertThat(post.lastActivityDate).isEqualTo("2014-01-21T21:05:11.577")
        assertHasUser1Fields(post)
        assertThat(post.comments).hasSize(1)
        assertHasComment3(post.comments)
    }

    fun getAnswerWithUid(posts: List<Answer>, uid: String): Answer {
        var answer = posts.find { it.uid == uid }
        if (answer == null) {
            fail("Could not find post[$uid]. Posts found: ${posts.map { it.uid }}")
        } else {
            return answer!!
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    //COMMENTS
    /////////////////////////////////////////////////////////////////////////////

    fun assertHasComment1(comments: List<Comment>) {
        var comment = getCommentWithUid(comments, "c$indexedSiteId.1")
        assertThat(comment.uid).isEqualTo("c$indexedSiteId.1")
        assertThat(comment.postUid).isEqualTo("p$indexedSiteId.1")
        assertThat(comment.score).isEqualTo("4")
        assertThat(comment.textContent).isEqualTo("Not so sure, let's have a discussion: [Should Food/Beer pairings be on topic?](http://meta.beer.stackexchange.com/q/11)")
        assertThat(comment.creationDate).isEqualTo("2014-01-21T21:53:17.400")
        assertThat(comment.userUid).isEqualTo("u$indexedSiteId.1")
        assertHasUser1Fields(comment)
    }

    fun assertHasComment2(comments: List<Comment>) {
        var comment = getCommentWithUid(comments, "c$indexedSiteId.2")
        assertThat(comment.uid).isEqualTo("c$indexedSiteId.2")
        assertThat(comment.postUid).isEqualTo("p$indexedSiteId.1")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.textContent).isEqualTo("possible duplicate of [Proposal: \\[pairing\\] is a bad tag and should be removed](http://meta.beer.stackexchange.com/questions/10/proposal-pairing-is-a-bad-tag-and-should-be-removed)")
        assertThat(comment.creationDate).isEqualTo("2014-01-21T21:54:57.053")
        assertThat(comment.userUid).isEqualTo("u$indexedSiteId.2")
        assertHasUser2Fields(comment)
    }

    fun assertHasComment3(comments: List<Comment>) {
        var comment = getCommentWithUid(comments, "c$indexedSiteId.3")
        assertThat(comment.uid).isEqualTo("c$indexedSiteId.3")
        assertThat(comment.postUid).isEqualTo("p$indexedSiteId.2")
        assertThat(comment.score).isEqualTo("3")
        assertThat(comment.textContent).isEqualTo("Not a duplicate, although the outcome of that discussion *should* hinge on this one.")
        assertThat(comment.creationDate).isEqualTo("2014-01-21T22:01:41.543")
        assertThat(comment.userUid).isEqualTo("u$indexedSiteId.3")
        assertHasUser3Fields(comment)
    }

    private fun getCommentWithUid(comments: List<Comment>, uid: String): Comment {
        var comment = comments.find { it.uid == uid }
        if (comment == null) {
            fail("Could not find post[$uid]. Posts found: ${comments.map { it.uid }}")
        } else {
            return comment!!
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    //USERS
    /////////////////////////////////////////////////////////////////////////////
    
    fun assertHasUser1Fields(user: ContainsPrimaryUserFields) {
        assertThat(user.userUid).isEqualTo("u$indexedSiteId.1")
        assertThat(user.userReputation).isEqualTo("101")
        assertThat(user.userDisplayName).isEqualTo("Geoff Dalgas")
    }

    fun assertHasUser2Fields(user: ContainsPrimaryUserFields) {
        assertThat(user.userUid).isEqualTo("u$indexedSiteId.2")
        assertThat(user.userDisplayName).isEqualTo("Kasra Rahjerdi")
        assertThat(user.userReputation).isEqualTo("268")
    }

    fun assertHasUser3Fields(user: ContainsPrimaryUserFields) {
        assertThat(user.userUid).isEqualTo("u$indexedSiteId.3")
        assertThat(user.userDisplayName).isEqualTo("Adam Lear")
        assertThat(user.userReputation).isEqualTo("99")
    }
}