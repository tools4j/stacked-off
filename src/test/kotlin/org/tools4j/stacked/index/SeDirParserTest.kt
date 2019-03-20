package org.tools4j.stacked.index

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class SeDirParserTest {
    private val indexes = createIndexes()
    private lateinit var users: MutableList<User>
    private lateinit var comments: MutableList<RawComment>
    private lateinit var posts: MutableList<RawPost>
    private lateinit var seDirParser: SeDirParser

    @BeforeEach
    fun setup(){
        users = ArrayList()
        comments = ArrayList()
        posts = ArrayList()
        seDirParser = SeDirParser(
            SeZipFileParser(
                SeFileInZipParserProviderImpl(getXmlRowHandlers())), indexes)
    }

    @Test
    public fun testWithSingleZipsPerSite(){
        loadFromDirAndAssertSitesLoaded("/data/se-example-dir-5")
    }

    @Test
    public fun testWithMultipleZipsPerSite(){
        loadFromDirAndAssertSitesLoaded("/data/se-example-dir-6")
    }

    private fun loadFromDirAndAssertSitesLoaded(classpath: String) {
        val path = File(this.javaClass.getResource(classpath).toURI())
        seDirParser.parse(path.absolutePath, {true});
        assertSite1AndSite2Loaded()
    }

    private fun assertSite1AndSite2Loaded() {
        println(indexes.indexedSiteIndex.getAll())
        val coffeeIndexedSite = indexes.indexedSiteIndex.getByTinyName("coffeeme")!!
        val beerIndexedSite = indexes.indexedSiteIndex.getByTinyName("beerme")!!

        val coffeeAssertions = CoffeeSiteAssertions(coffeeIndexedSite.indexedSiteId)
        val beerAssertions = BeerSiteAssertions(beerIndexedSite.indexedSiteId)

        coffeeAssertions.assertHasAllRawPosts(posts)
        coffeeAssertions.assertHasAllComments(comments)
        coffeeAssertions.assertHasAllUsers(users)

        beerAssertions.assertHasAllRawPosts(posts)
        beerAssertions.assertHasAllComments(comments)
        beerAssertions.assertHasAllUsers(users)
    }

    private fun getXmlRowHandlers(): Map<String, () -> XmlRowHandler<out Any>> {
        return mapOf(
            "Users.xml" to { UserXmlRowHandler { ToListHandler(users) } },
            "Posts.xml" to { PostXmlRowHandler { ToListHandler(posts) } },
            "Comments.xml" to { CommentXmlRowHandler { ToListHandler(comments) } })
    }
}