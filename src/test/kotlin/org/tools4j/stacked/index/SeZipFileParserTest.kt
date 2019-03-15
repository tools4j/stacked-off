package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SeZipFileParserTest {
    private val s1 = CoffeeSiteAssertions()
    private val s2 = BeerSiteAssertions()
    private lateinit var users: MutableList<User>
    private lateinit var comments: MutableList<RawComment>
    private lateinit var posts: MutableList<RawPost>

    @BeforeEach
    fun setup(){
        users = ArrayList()
        comments = ArrayList()
        posts = ArrayList()
    }

    private fun createSeHandlerProvider(indexedSiteId: String): SeHandlerProviderImpl {
        return SeHandlerProviderImpl(
            indexedSiteId,
            mapOf(
                "Users.xml" to { UserXmlRowHandler { ToListHandler(users) }},
                "Posts.xml" to { PostXmlRowHandler { ToListHandler(posts) }},
                "Comments.xml" to { CommentXmlRowHandler { ToListHandler(comments) }})
        )
    }

    @Test
    fun parseCoffee7z() {
        val seHandlerProvider = createSeHandlerProvider("1")
        val zipFile = getFileOnClasspath(this.javaClass,"/data/se-example-dir-5/coffee.meta.stackexchange.com.7z")
        SeZipFileParser(seHandlerProvider).parse(zipFile.absolutePath)
        assertCoffeeContents()
    }

    @Test
    fun parseCoffeeZip() {
        val seHandlerProvider = createSeHandlerProvider("1")
        val zipFile = getFileOnClasspath(this.javaClass,"/data/se-example-dir-4/coffee.meta.stackexchange.com.zip")
        SeZipFileParser(seHandlerProvider).parse(zipFile.absolutePath)
        assertCoffeeContents()
    }

    @Test
    fun parseBeer7z() {
        val seHandlerProvider = createSeHandlerProvider("2")
        val zipFile = getFileOnClasspath(this.javaClass,"/data/se-example-dir-4/beer.meta.stackexchange.com.7z")
        SeZipFileParser(seHandlerProvider).parse(zipFile.absolutePath)
        assertBeerContents()
    }

    @Test
    fun parseBeerZip() {
        val seHandlerProvider = createSeHandlerProvider("2")
        val zipFile = getFileOnClasspath(this.javaClass,"/data/se-example-dir-5/beer.meta.stackexchange.com.zip")
        SeZipFileParser(seHandlerProvider).parse(zipFile.absolutePath)
        assertBeerContents()
    }

    private fun assertCoffeeContents(){
        assertThat(users).hasSize(6)
        s1.assertHasAllUsers(users)

        assertThat(comments).hasSize(9)
        s1.assertHasAllComments(comments)

        assertThat(posts).hasSize(3)
        s1.assertHasAllRawPosts(posts)
    }

    private fun assertBeerContents() {
        assertThat(users).hasSize(3)
        s2.assertHasAllUsers(users)

        assertThat(comments).hasSize(3)
        s2.assertHasAllComments(comments)

        assertThat(posts).hasSize(3)
        s2.assertHasAllRawPosts(posts)
    }
}