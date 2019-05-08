package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SeZipFileParserTest {
    private val s1 = CoffeeStagingAssertions()
    private val s2 = BeerStagingAssertions()
    private lateinit var users: MutableList<StagingUser>
    private lateinit var comments: MutableList<StagingComment>
    private lateinit var posts: MutableList<StagingPost>

    @BeforeEach
    fun setup(){
        users = ArrayList()
        comments = ArrayList()
        posts = ArrayList()
    }

    private fun createSeHandlerProvider(): SeFileInZipParserProvider {
        return SeFileInZipParserProviderImpl(
            mapOf(
                "Users.xml" to UserXmlRowHandler(ToListHandler(users)),
                "Posts.xml" to PostXmlRowHandler(ToListHandler(posts)),
                "Comments.xml" to CommentXmlRowHandler(ToListHandler(comments)))
        )
    }

    @Test
    fun parseCoffee7z() {
        val seHandlerProvider = createSeHandlerProvider()
        val zipFile = getFileOnClasspath(this.javaClass,"/data/se-example-dir-5/coffee.meta.stackexchange.com.7z")
        SeZipFileParser(seHandlerProvider).parse(zipFile.absolutePath)
        assertCoffeeContents()
    }

    @Test
    fun parseCoffeeZip() {
        val seHandlerProvider = createSeHandlerProvider()
        val zipFile = getFileOnClasspath(this.javaClass,"/data/se-example-dir-4/coffee.meta.stackexchange.com.zip")
        SeZipFileParser(seHandlerProvider).parse(zipFile.absolutePath)
        assertCoffeeContents()
    }

    @Test
    fun parseBeer7z() {
        val seHandlerProvider = createSeHandlerProvider()
        val zipFile = getFileOnClasspath(this.javaClass,"/data/se-example-dir-4/beer.meta.stackexchange.com.7z")
        SeZipFileParser(seHandlerProvider).parse(zipFile.absolutePath)
        assertBeerContents()
    }

    @Test
    fun parseBeerZip() {
        val seHandlerProvider = createSeHandlerProvider()
        val zipFile = getFileOnClasspath(this.javaClass,"/data/se-example-dir-5/beer.meta.stackexchange.com.zip")
        SeZipFileParser(seHandlerProvider).parse(zipFile.absolutePath)
        assertBeerContents()
    }

    @Test
    fun parseCoffeeZip_badTagInPosts() {
        val seHandlerProvider = createSeHandlerProvider()
        val zipFile = getFileOnClasspath(this.javaClass,"/data/se-example-dir-9/coffee.bad.tag.in.Posts.xml.7z")
        assertThatCode({SeZipFileParser(seHandlerProvider).parse(zipFile.absolutePath)})
            .hasMessageMatching("Found non 'row' child element within \\[posts\\] with name \\[NotARow\\] child number \\[2\\] in file \\[Posts.xml\\] whilst parsing archive \\[[^\\]]*coffee.bad.tag.in.Posts.xml.7z\\]")
    }

    @Test
    fun parseCoffeeZip_badlyFormedXml() {
        val seHandlerProvider = createSeHandlerProvider()
        val zipFile = getFileOnClasspath(this.javaClass,"/data/se-example-dir-9/coffee.badly.formed.xml.in.Users.xml.7z")
        assertThatCode({SeZipFileParser(seHandlerProvider).parse(zipFile.absolutePath)})
            .hasMessageMatching("Found non 'row' child element within \\[users\\] with name \\[rowasdf\\] child number \\[1\\] in file \\[Users.xml\\] whilst parsing archive \\[[^\\]]*coffee.badly.formed.xml.in.Users.xml.7z\\]")
    }

    @Test
    fun parseCoffeeZip_rubbishInPostsXml() {
        val seHandlerProvider = createSeHandlerProvider()
        val zipFile = getFileOnClasspath(this.javaClass,"/data/se-example-dir-9/coffee.rubbish.in.Posts.xml.7z")
        assertThatCode({SeZipFileParser(seHandlerProvider).parse(zipFile.absolutePath)})
            .hasMessageMatching("Error parsing file: Unexpected character .*? in prolog.*?expected '\\<'\\s*.*? in file \\[Posts.xml\\] whilst parsing archive \\[[^\\]]*coffee.rubbish.in.Posts.xml.7z\\]")
    }

    @Test
    fun parseCoffeeZip_corrupted() {
        val seHandlerProvider = createSeHandlerProvider()
        val zipFile = getFileOnClasspath(this.javaClass,"/data/se-example-dir-9/coffee.corrupted.7z")
        assertThatCode({SeZipFileParser(seHandlerProvider).parse(zipFile.absolutePath)})
            .hasMessageMatching("Error occurred whilst parsing file \\[[^\\]]*coffee\\.corrupted\\.7z\\] Archive file can't be opened with none of the registered codecs")
    }

    @Test
    fun parseCoffeeZip_emptyXml() {
        val seHandlerProvider = createSeHandlerProvider()
        val zipFile = getFileOnClasspath(this.javaClass,"/data/se-example-dir-9/coffee.empty.Comments.xml.7z")
        assertThatCode({SeZipFileParser(seHandlerProvider).parse(zipFile.absolutePath)})
            .hasMessageMatching("Error parsing file: Unexpected character .*? in prolog.*?expected '\\<'\\s*.*? in file \\[Comments.xml\\] whilst parsing archive \\[[^\\]]*coffee.empty.Comments.xml.7z\\]")
    }

    private fun assertCoffeeContents(){
        assertThat(users).hasSize(6)
        s1.assertHasAllUsers(users)

        assertThat(comments).hasSize(9)
        s1.assertHasAllComments(comments)

        assertThat(posts).hasSize(3)
        s1.assertHasAllPosts(posts)
    }

    private fun assertBeerContents() {
        assertThat(users).hasSize(3)
        s2.assertHasAllUsers(users)

        assertThat(comments).hasSize(3)
        s2.assertHasAllComments(comments)

        assertThat(posts).hasSize(3)
        s2.assertHasAllPosts(posts)
    }
}