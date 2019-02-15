package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.fail

class CommentTestUtils {
    companion object {

        @JvmStatic
        fun assertHasAllComments(comments: List<Comment>) {
            assertHasComment1(comments)
            assertHasComment2(comments)
            assertHasComment3(comments)
            assertHasComment4(comments)
            assertHasComment6(comments)
            assertHasComment7(comments)
            assertHasComment8(comments)
            assertHasComment9(comments)
        }

        @JvmStatic
        fun assertHasComment1(comments: List<Comment>) {
            var comment = getCommentWithId(comments, "1")
            assertThat(comment.id).isEqualTo("1")
            assertThat(comment.postId).isEqualTo("3")
            assertThat(comment.score).isEqualTo("0")
            assertThat(comment.text).isEqualTo("*Wave* hello fellow rpg.se user.")
            assertThat(comment.creationDate).isEqualTo("2015-01-27T21:31:29.540")
            assertThat(comment.userId).isEqualTo("24")
        }
        
        @JvmStatic
        fun assertHasComment2(comments: List<Comment>) {
            var comment = getCommentWithId(comments, "2")
            assertThat(comment.id).isEqualTo("2")
            assertThat(comment.postId).isEqualTo("3")
            assertThat(comment.score).isEqualTo("0")
            assertThat(comment.text).isEqualTo("@JoshuaAslanSmith: Cheers. :)")
            assertThat(comment.creationDate).isEqualTo("2015-01-27T22:17:51.780")
            assertThat(comment.userId).isEqualTo("3")
        }
        
        @JvmStatic
        fun assertHasComment3(comments: List<Comment>) {
            var comment = getCommentWithId(comments, "3")
            assertThat(comment.id).isEqualTo("3")
            assertThat(comment.postId).isEqualTo("2")
            assertThat(comment.score).isEqualTo("2")
            assertThat(comment.text).isEqualTo("I've gone ahead and added a feed for this site (meta.coffee) already, since meta's a very important part of the community.")
            assertThat(comment.creationDate).isEqualTo("2015-01-27T22:33:57.407")
            assertThat(comment.userId).isEqualTo("63")
        }

        @JvmStatic
        fun assertHasComment4(comments: List<Comment>) {
            var comment = getCommentWithId(comments, "4")
            assertThat(comment.id).isEqualTo("4")
            assertThat(comment.postId).isEqualTo("1")
            assertThat(comment.score).isEqualTo("0")
            assertThat(comment.text).isEqualTo("oh, these are terms for _that_? never heard either… maybe allow both and recommend elaboration in the text?")
            assertThat(comment.creationDate).isEqualTo("2015-01-27T22:55:08.750")
            assertThat(comment.userId).isEqualTo("77")
        }

        @JvmStatic
        fun assertHasComment5(comments: List<Comment>) {
            var comment = getCommentWithId(comments, "5")
            assertThat(comment.id).isEqualTo("5")
             assertThat(comment.postId).isEqualTo("2")
             assertThat(comment.score).isEqualTo("0")
             assertThat(comment.text).isEqualTo("@Doorknob add that as an answer so I can upvote (for the meta rep as much as that mattters lol)\")")
             assertThat(comment.creationDate).isEqualTo("2015-01-27T22:56:19.410")
             assertThat(comment.userId).isEqualTo("24")
        }
  
        @JvmStatic
        fun assertHasComment6(comments: List<Comment>) {
            var comment = getCommentWithId(comments, "6")
            assertThat(comment.id).isEqualTo("6")
            assertThat(comment.postId).isEqualTo("1")
            assertThat(comment.score).isEqualTo("0")
            assertThat(comment.text).isEqualTo("Recommending both would be confusing; generally you want to use one tag, and make any other names a tag synonym (you don't want to have half the questions have one name, and the other have a different one, making it hard to get a list of all questions about that particular topic).")
            assertThat(comment.creationDate).isEqualTo("2015-01-28T01:48:22.847")
            assertThat(comment.userId).isEqualTo("80")
        }

        @JvmStatic
        fun assertHasComment7(comments: List<Comment>) {
            var comment = getCommentWithId(comments, "7")
            assertThat(comment.id).isEqualTo("7")
            assertThat(comment.postId).isEqualTo("1")
            assertThat(comment.score).isEqualTo("0")
            assertThat(comment.text).isEqualTo("Certainly, the terms \"pour-over\", \"drip\", and \"filter\" (and perhaps others...) need regional disambiguation; perhaps this should be a question at [main] main site? I fear clarifying text in every question/answer will be necessary. Making uniform the vernacular of the entire world might be easier. ;-)")
            assertThat(comment.creationDate).isEqualTo("2015-02-11T16:32:01.823")
            assertThat(comment.userId).isEqualTo("262")
        }

        @JvmStatic
        fun assertHasComment8(comments: List<Comment>) {
            var comment = getCommentWithId(comments, "8")
            assertThat(comment.id).isEqualTo("8")
            assertThat(comment.postId).isEqualTo("1")
            assertThat(comment.score).isEqualTo("1")
            assertThat(comment.text).isEqualTo("@hoc_age Tagging terminology needs to be nailed down in the tag wiki for the site so for our purposes Yes, it does need to be agreed upon and defined. Meta is the place to talk about and discuss issues on the main site.")
            assertThat(comment.creationDate).isEqualTo("2015-02-11T17:02:35.463")
            assertThat(comment.userId).isEqualTo("24")
        }
  
        @JvmStatic
        fun assertHasComment9(comments: List<Comment>) {
            var comment = getCommentWithId(comments, "9")
            assertThat(comment.id).isEqualTo("9")
            assertThat(comment.postId).isEqualTo("1")
            assertThat(comment.score).isEqualTo("0")
            assertThat(comment.text).isEqualTo("@JoshuaAslanSmith - agreed about the tag terminology, and I think  you've convinced me that the best place to \"define\" our *lingua franca* is the tag wiki. My comment on [this answer](http://meta.coffee.stackexchange.com/a/24/262) summarizes my feelings about the (more-)general problem.")
            assertThat(comment.creationDate).isEqualTo("2015-02-11T17:09:52.737")
            assertThat(comment.userId).isEqualTo("262")
        }

        private fun getCommentWithId(comments: List<Comment>, id: String): Comment {
            var comment = comments.find { it.id == id }
            if (comment == null) {
                fail("Could not find post[$id]. Posts found: ${comments.map { it.id }}")
            } else {
                return comment!!
            }
        }
    }
}