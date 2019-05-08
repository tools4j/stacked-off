package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.fail

class CoffeeSiteAssertions(val indexedSiteId: String) {
    constructor(): this(coffeeSiteIndexUtils.siteId)

    /////////////////////////////////////////////////////////////////////////////
    //QUESTIONS
    /////////////////////////////////////////////////////////////////////////////

    fun assertHasAllQuestions(questions: List<Question>) {
        assertHasQuestion1(questions)
        assertHasQuestion2(questions)
    }

    fun assertHasQuestion1(questions: Collection<Question>){
        val question = getQuestionByUid("p$indexedSiteId.1", questions)
        assertThat(question).isNotNull
        assertIsQuestion1(question!!)
    }

    fun assertHasQuestion2(questions: Collection<Question>){
        val question = getQuestionByUid("p$indexedSiteId.2", questions)
        assertThat(question).isNotNull
        assertIsQuestion2(question!!)
    }

    fun getQuestionByUid(uid: String, questions: Collection<Question>): Question? {
        return questions.firstOrNull { it.uid == uid }
    }

    fun assertIsQuestion1(question: Question) {
        assertThat(question.uid).isEqualTo("p$indexedSiteId.1")
        assertThat(question.indexedSite.indexedSiteId).isEqualTo(indexedSiteId)
        assertThat(question.creationDate).isEqualTo("2015-01-27T21:23:05.507")
        assertThat(question.score).isEqualTo("7")
        assertThat(question.viewCount).isEqualTo("259")
        assertThat(question.body).isEqualTo("<p>While answering a few of <a href=\"https://coffee.stackexchange.com/users/8/edchum\">EdChum</a>'s questions I discovered that what I/we in the USA call pour over coffee is referred to as drip coffee in the UK. I added the pour-over tag to both questions I encountered but figured we should decide as a community which tag to use to describe this brewing process and then properly document it because drip-coffee means something different in the US (which is apparently referred to as filter-cofee in the UK). For clarification the method in question is shown in the image below. </p>\n\n<p><img src=\"https://i.stack.imgur.com/8BYnT.jpg\" alt=\"enter image description here\"> </p>\n")
        assertThat(question.userUid).isEqualTo("u$indexedSiteId.2")
        assertThat(question.lastActivityDate).isEqualTo("2015-01-28T01:53:35.523")
        assertThat(question.tags).isEqualTo("<discussion><tags>")
        assertThat(question.favoriteCount).isNull()
        assertThat(question.title).isEqualTo("Should we describe the process of brewing a single cup via pouring water over ground coffee as pour-over-coffee or drip-coffee?")
        assertHasUser2Fields(question)

        assertThat(question.comments).hasSize(5)
        assertHasComment4(question.comments)
        assertHasComment6(question.comments)
        assertHasComment7(question.comments)
        assertHasComment8(question.comments)
        assertHasComment9(question.comments)

        assertThat(question.answers).hasSize(1)
        assertHasAnswer3(question.answers)
    }

    fun assertIsQuestion2(question: Question) {
        assertThat(question.uid).isEqualTo("p$indexedSiteId.2")
        assertThat(question.indexedSite.indexedSiteId).isEqualTo(indexedSiteId)
        assertThat(question.creationDate).isEqualTo("2015-01-27T21:26:10.227")
        assertThat(question.score).isEqualTo("5")
        assertThat(question.viewCount).isEqualTo("49")
        assertThat(question.body).isEqualTo("<p>Being newly created brewing coffee we have zero feeds appearing in our main chat right now. What blogs, news sites, or other important coffee related things should appear in our main chat room's feed? Post your suggestions/submissions.  </p>\n")
        assertThat(question.userUid).isEqualTo("u$indexedSiteId.2")
        assertThat(question.lastActivityDate).isEqualTo("2015-02-06T14:14:32.833")
        assertThat(question.tags).isEqualTo("<discussion>")
        assertThat(question.favoriteCount).isEqualTo("0")
        assertThat(question.title).isEqualTo("What should go in our main chat feeds?")
        assertHasUser2Fields(question)

        assertThat(question.comments).hasSize(2)
        assertHasComment3(question.comments)
        assertHasComment5(question.comments)

        assertThat(question.answers).isEmpty()
    }

    /////////////////////////////////////////////////////////////////////////////
    //ANSWERS
    /////////////////////////////////////////////////////////////////////////////

    fun assertHasAnswer3(answers: List<Answer>) {
        val answer = getAnswerWithUid(answers, "p$indexedSiteId.3")
        assertIsAnswer3(answer)
    }

    fun assertIsAnswer3(answer: Answer) {
        assertThat(answer.uid).isEqualTo("p$indexedSiteId.3")
        assertThat(answer.creationDate).isEqualTo("2015-01-27T21:30:20.953")
        assertThat(answer.score).isEqualTo("8")
        assertThat(answer.body).isEqualTo("<p>It looks like blah filter coffee has <a href=\"http://en.wikipedia.org/wiki/Indian_filter_coffee\" rel=\"nofollow\">another, different meaning</a> too. When I read \"drip coffee,\" I think of the kind you <a href=\"http://en.wikipedia.org/wiki/Drip_brew\" rel=\"nofollow\">get from a traditional coffeemaker</a>. Go for \"pour-over.\"</p>\n")
        assertThat(answer.userUid).isEqualTo("u$indexedSiteId.1")
        assertThat(answer.lastActivityDate).isEqualTo("2015-01-27T21:30:20.953")
        assertThat(answer.parentUid).isEqualTo("p$indexedSiteId.1")
        assertThat(answer.favoriteCount).isNull()
        assertHasUser1Fields(answer)

        assertThat(answer.comments).hasSize(2)
        assertHasComment1(answer.comments)
        assertHasComment2(answer.comments)
    }

    /////////////////////////////////////////////////////////////////////////////
    //COMMENTS
    /////////////////////////////////////////////////////////////////////////////

    fun assertHasComment1(comments: List<Comment>) {
        var comment = getCommentWithUid(comments, "c$indexedSiteId.1")
        assertThat(comment.uid).isEqualTo("c$indexedSiteId.1")
        assertThat(comment.postUid).isEqualTo("p$indexedSiteId.3")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("*Wave* hello fellow rpg.se user.")
        assertThat(comment.creationDate).isEqualTo("2015-01-27T21:31:29.540")
        assertThat(comment.userUid).isEqualTo("u$indexedSiteId.2")
        //TODO add extra user fields
        assertHasUser2Fields(comment)
    }

    fun assertHasComment2(comments: List<Comment>) {
        var comment = getCommentWithUid(comments, "c$indexedSiteId.2")
        assertThat(comment.uid).isEqualTo("c$indexedSiteId.2")
        assertThat(comment.postUid).isEqualTo("p$indexedSiteId.3")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("@JoshuaAslanSmith: Cheers. :)")
        assertThat(comment.creationDate).isEqualTo("2015-01-27T22:17:51.780")
        assertThat(comment.userUid).isEqualTo("u$indexedSiteId.1")
        assertHasUser1Fields(comment)
    }

    fun assertHasComment3(comments: List<Comment>) {
        var comment = getCommentWithUid(comments, "c$indexedSiteId.3")
        assertThat(comment.uid).isEqualTo("c$indexedSiteId.3")
        assertThat(comment.postUid).isEqualTo("p$indexedSiteId.2")
        assertThat(comment.score).isEqualTo("2")
        assertThat(comment.text).isEqualTo("I've gone ahead and added a feed for this site (meta.coffee) already, since meta's a very important part of the community.")
        assertThat(comment.creationDate).isEqualTo("2015-01-27T22:33:57.407")
        assertThat(comment.userUid).isEqualTo("u$indexedSiteId.3")
        assertHasUser3Fields(comment)
    }

    fun assertHasComment4(comments: List<Comment>) {
        var comment = getCommentWithUid(comments, "c$indexedSiteId.4")
        assertThat(comment.uid).isEqualTo("c$indexedSiteId.4")
        assertThat(comment.postUid).isEqualTo("p$indexedSiteId.1")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("oh, these are terms for _that_? never heard either… maybe allow both and recommend elaboration in the text?")
        assertThat(comment.creationDate).isEqualTo("2015-01-27T22:55:08.750")
        assertThat(comment.userUid).isEqualTo("u$indexedSiteId.4")
        assertHasUser4Fields(comment)
    }

    fun assertHasComment5(comments: List<Comment>) {
        var comment = getCommentWithUid(comments, "c$indexedSiteId.5")
        assertThat(comment.uid).isEqualTo("c$indexedSiteId.5")
        assertThat(comment.postUid).isEqualTo("p$indexedSiteId.2")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("@Doorknob add that as an answer so I can upvote (for the meta rep as much as that mattters lol)\\")
        assertThat(comment.creationDate).isEqualTo("2015-01-27T22:56:19.410")
        assertThat(comment.userUid).isEqualTo("u$indexedSiteId.2")
        assertHasUser2Fields(comment)
    }

    fun assertHasComment6(comments: List<Comment>) {
        var comment = getCommentWithUid(comments, "c$indexedSiteId.6")
        assertThat(comment.uid).isEqualTo("c$indexedSiteId.6")
        assertThat(comment.postUid).isEqualTo("p$indexedSiteId.1")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("Recommending both would be confusing; generally you want to use one tag, and make any other names a tag synonym (you don't want to have half the questions have one name, and the other have a different one, making it hard to get a list of all questions about that particular topic).")
        assertThat(comment.creationDate).isEqualTo("2015-01-28T01:48:22.847")
        assertThat(comment.userUid).isEqualTo("u$indexedSiteId.5")
        assertHasUser5Fields(comment)
    }

    fun assertHasComment7(comments: List<Comment>) {
        var comment = getCommentWithUid(comments, "c$indexedSiteId.7")
        assertThat(comment.uid).isEqualTo("c$indexedSiteId.7")
        assertThat(comment.postUid).isEqualTo("p$indexedSiteId.1")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("Certainly, the terms \"pour-over\", \"drip\", and \"filter\" (and perhaps others...) need regional disambiguation; perhaps this should be a question at [main] main site? I fear clarifying text in every question/answer will be necessary. Making uniform the vernacular of the entire world might be easier. ;-)")
        assertThat(comment.creationDate).isEqualTo("2015-02-11T16:32:01.823")
        assertThat(comment.userUid).isEqualTo("u$indexedSiteId.6")
        assertHasUser6Fields(comment)
    }

    fun assertHasComment8(comments: List<Comment>) {
        var comment = getCommentWithUid(comments, "c$indexedSiteId.8")
        assertThat(comment.uid).isEqualTo("c$indexedSiteId.8")
        assertThat(comment.postUid).isEqualTo("p$indexedSiteId.1")
        assertThat(comment.score).isEqualTo("1")
        assertThat(comment.text).isEqualTo("@hoc_age Tagging terminology needs to be nailed down in the tag wiki for the site so for our purposes Yes, it does need to be agreed upon and defined. Meta is the place to talk about and discuss issues on the main site.")
        assertThat(comment.creationDate).isEqualTo("2015-02-11T17:02:35.463")
        assertThat(comment.userUid).isEqualTo("u$indexedSiteId.2")
        assertHasUser2Fields(comment)
    }

    fun assertHasComment9(comments: List<Comment>) {
        var comment = getCommentWithUid(comments, "c$indexedSiteId.9")
        assertThat(comment.uid).isEqualTo("c$indexedSiteId.9")
        assertThat(comment.postUid).isEqualTo("p$indexedSiteId.1")
        assertThat(comment.score).isEqualTo("0")
        assertThat(comment.text).isEqualTo("@JoshuaAslanSmith - agreed about the tag terminology, and I think  you've convinced me that the best place to \"define\" our *lingua franca* is the tag wiki. My comment on [this answer](http://meta.coffee.stackexchange.com/a/24/262) summarizes my feelings about the (more-)general problem.")
        assertThat(comment.creationDate).isEqualTo("2015-02-11T17:09:52.737")
        assertThat(comment.userUid).isEqualTo("u$indexedSiteId.6")
        assertHasUser6Fields(comment)
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
        assertThat(user.userReputation).isEqualTo("103")
        assertThat(user.userDisplayName).isEqualTo("Jadasc")
    }

    fun assertHasUser2Fields(element: ContainsPrimaryUserFields) {
        assertThat(element.userUid).isEqualTo("u$indexedSiteId.2")
        assertThat(element.userReputation).isEqualTo("1241")
        assertThat(element.userDisplayName).isEqualTo("Joshua Aslan Smith")
    }

    fun assertHasUser3Fields(element: ContainsPrimaryUserFields) {
        assertThat(element.userUid).isEqualTo("u$indexedSiteId.3")
        assertThat(element.userReputation).isEqualTo("101")
        assertThat(element.userDisplayName).isEqualTo("Doorknob")
    }

    fun assertHasUser4Fields(element: ContainsPrimaryUserFields) {
        assertThat(element.userUid).isEqualTo("u$indexedSiteId.4")
        assertThat(element.userReputation).isEqualTo("101")
        assertThat(element.userDisplayName).isEqualTo("mirabilos")
    }

    fun assertHasUser5Fields(element: ContainsPrimaryUserFields) {
        assertThat(element.userUid).isEqualTo("u$indexedSiteId.5")
        assertThat(element.userReputation).isEqualTo("488")
        assertThat(element.userDisplayName).isEqualTo("Sam Whited")
    }

    fun assertHasUser6Fields(element: ContainsPrimaryUserFields) {
        assertThat(element.userUid).isEqualTo("u$indexedSiteId.6")
        assertThat(element.userReputation).isEqualTo("6844")
        assertThat(element.userDisplayName).isEqualTo("hoc_age")
    }

    fun getQuestionWithUid(questions: List<Question>, uid: String): Question {
        var question = questions.find { it.uid == uid }
        if (question == null) {
            fail("Could not find question[$uid]. Questions found: ${questions.map { it.uid }}")
        } else {
            return question!!
        }
    }

    fun getAnswerWithUid(answers: List<Answer>, uid: String): Answer {
        var answer = answers.find { it.uid == uid }
        if (answer == null) {
            fail("Could not find answer[$uid]. Answers found: ${answers.map { it.uid }}")
        } else {
            return answer!!
        }
    }
}