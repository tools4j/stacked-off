package org.tools4j.stacked.index

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

class SeDirZipParserTest {
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

    @Test
    public fun testWithSingleZipsPerSite(){
        loadFromDirAndAssertSitesLoaded("/data/se-example-dir-5")
    }

    @Test
    public fun testWithMultipleZipsPerSite(){
        loadFromDirAndAssertSitesLoaded("/data/se-example-dir-6")
    }

    private fun loadFromDirAndAssertSitesLoaded(dirPath: String) {
        val dir = getFileOnClasspath(dirPath).absolutePath
        val dirContents = SeDir(dir).getContents()
        val seDirSites = dirContents.getSites()
        for (seDirSite in seDirSites) {
            val indexingSite = IndexingSiteImpl(
                getIndexedSiteId(seDirSite.site.tinyName),
                "2019-10-11T10:00:00", seDirSite.site
            )
            val handler = createSeHandlerProvider(indexingSite.indexedSiteId)
            val zipFileParser = SeZipFileParser(handler)
            for (zipFile in seDirSite.zipFiles) {
                zipFileParser.parse(zipFile.absolutePath)
            }
        }
        assertSite1AndSite2Loaded()
    }

    private fun assertSite1AndSite2Loaded() {
        s1.assertHasAllRawPosts(posts)
        s1.assertHasAllComments(comments)
        s1.assertHasAllUsers(users)

        s2.assertHasAllRawPosts(posts)
        s2.assertHasAllComments(comments)
        s2.assertHasAllUsers(users)
    }

    private fun getIndexedSiteId(tinyName: String): String {
        if(tinyName == "coffeeme") return "1"
        else if(tinyName == "beerme") return "2"
        else throw IllegalArgumentException("Unknown tinyName for site $tinyName")
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
}