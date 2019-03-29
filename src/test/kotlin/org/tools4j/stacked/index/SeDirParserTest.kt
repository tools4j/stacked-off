package org.tools4j.stacked.index

import org.apache.lucene.index.Term
import org.assertj.core.api.Assertions.assertThat
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
        seDirParser.parseFromClasspath("/data/se-example-dir-5", {true})
        assertBeerAndCoffeeSitesLoaded()
    }

    @Test
    public fun testWithMultipleZipsPerSite(){
        seDirParser.parseFromClasspath("/data/se-example-dir-6", {true})
        assertBeerAndCoffeeSitesLoaded()
    }

    @Test
    fun testLoadingOfDirectoryWithZipFileContainingPostXmlWithBadTag(){
        seDirParser.parseFromClasspath("/data/se-example-dir-7", {true});
        assertBeerSiteLoaded()
        assertCoffeeSiteLoadedWithErrorMatching("Found non 'row' child element within \\[posts\\] with name \\[NotARow\\] child number \\[2\\] in file \\[Posts.xml\\] whilst parsing archive \\[[^\\]]*coffee.meta.stackexchange.com.7z\\]")
    }

    @Test
    fun testLoadingOfDirectoryWithZipFileContainingUsersXmlWithBadlyFormedXml(){
        seDirParser.parseFromClasspath("/data/se-example-dir-8", {true});
        assertBeerSiteLoaded()
        assertCoffeeSiteLoadedWithErrorMatching("Found non 'row' child element within \\[users\\] with name \\[rowasdf\\] child number \\[1\\] in file \\[Users.xml\\] whilst parsing archive \\[[^\\]]*]")
    }

    @Test
    fun testLoadingOfDirectoryWithBadZipFileThenLoadingWithGoodZipFile(){
        val pathWithBrokenCoffeeZipFile = "/data/se-example-dir-7"
        seDirParser.parseFromClasspath(pathWithBrokenCoffeeZipFile, {true});
        val originalBeerIndexedSite = assertBeerSiteLoaded()
        val coffeeIndexedSiteWithErrors = assertCoffeeSiteLoadedWithErrorMatching("Found non 'row' child element within \\[posts\\] with name \\[NotARow\\] child number \\[2\\] in file \\[Posts.xml\\] whilst parsing archive \\[[^\\]]*coffee.meta.stackexchange.com.7z\\]")

        val pathWithGoodCoffeeZipFile = "/data/se-example-dir-5"
        seDirParser.parseFromClasspath(pathWithGoodCoffeeZipFile, {seSite -> seSite.url.contains("coffee")});
        val coffeeIndexedSites = indexes.indexedSiteIndex.searchByTerm("tinyName", "coffeeme")
        assertThat(coffeeIndexedSites).hasSize(1)
        assertThat(coffeeIndexedSites.first().indexedSiteId).isNotEqualTo(coffeeIndexedSiteWithErrors)
        assertCoffeeSiteLoaded()

        val latestBeerIndexedSite = indexes.indexedSiteIndex.getByTinyName("beerme")!!
        assertThat(latestBeerIndexedSite.indexedSiteId).isEqualTo(originalBeerIndexedSite.indexedSiteId)
    }

    private fun assertThatSiteDoesNotHaveAnyEntities(indexedSiteId: String) {
        assertThat(indexes.postIndex.searchByTerm(Term("indexedSiteId", indexedSiteId))).isEmpty()
        assertThat(indexes.commentIndex.searchByTerm(Term("indexedSiteId", indexedSiteId))).isEmpty()
        assertThat(indexes.userIndex.searchByTerm(Term("indexedSiteId", indexedSiteId))).isEmpty()
    }

    private fun assertBeerAndCoffeeSitesLoaded() {
        assertBeerSiteLoaded()
        assertCoffeeSiteLoaded()
    }

    private fun assertCoffeeSiteLoaded(): IndexedSite {
        val coffeeIndexedSite = indexes.indexedSiteIndex.getByTinyName("coffeeme")!!
        val coffeeAssertions = CoffeeSiteAssertions(coffeeIndexedSite.indexedSiteId)

        coffeeAssertions.assertHasAllRawPosts(posts)
        coffeeAssertions.assertHasAllComments(comments)
        coffeeAssertions.assertHasAllUsers(users)
        return coffeeIndexedSite
    }

    private fun assertBeerSiteLoaded(): IndexedSite {
        val beerIndexedSite = indexes.indexedSiteIndex.getByTinyName("beerme")!!
        val beerAssertions = BeerSiteAssertions(beerIndexedSite.indexedSiteId)

        beerAssertions.assertHasAllRawPosts(posts)
        beerAssertions.assertHasAllComments(comments)
        beerAssertions.assertHasAllUsers(users)
        return beerIndexedSite
    }

    private fun getXmlRowHandlers(): Map<String, () -> XmlRowHandler<out Any>> {
        return mapOf(
            "Users.xml" to { UserXmlRowHandler { ToListHandler(users) } },
            "Posts.xml" to { PostXmlRowHandler { ToListHandler(posts) } },
            "Comments.xml" to { CommentXmlRowHandler { ToListHandler(comments) } })
    }

    private fun assertCoffeeSiteLoadedWithErrorMatching(message: String): IndexedSite {
        val coffeeIndexedSite = indexes.indexedSiteIndex.getByTinyName("coffeeme")!!
        assertThatSiteDoesNotHaveAnyEntities(coffeeIndexedSite.indexedSiteId)
        assertThat(coffeeIndexedSite.success).isFalse()
        assertThat(coffeeIndexedSite.errorMessage).matches(message)
        return coffeeIndexedSite
    }
}
