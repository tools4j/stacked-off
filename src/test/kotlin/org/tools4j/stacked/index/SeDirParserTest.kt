package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SeDirParserTest {
    private val indexes = createIndexes()
    private lateinit var seDirParser: SeDirParser

    @BeforeEach
    fun setup(){
        seDirParser = SeDirParser (
            SeZipFileParser(SeFileInZipParserProviderImpl(createXmlRowHandlers(indexes.stagingIndexes))),
            {indexes.indexedSiteIndex.getHighestIndexedSiteId()},
            SeDirParserListener(indexes)
        )
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

        val latestBeerIndexedSite = assertBeerSiteLoaded()
        assertThat(latestBeerIndexedSite.indexedSiteId).isEqualTo(originalBeerIndexedSite.indexedSiteId)
    }

    @Test
    fun testLoadingOfDirectoryWithGoodZipFileThenLoadingWithBadZipFile(){
        val pathWithGoodZipFiles = "/data/se-example-dir-5"
        seDirParser.parseFromClasspath(pathWithGoodZipFiles, {true});
        val originalCoffeeIndexedSite = assertCoffeeSiteLoaded()
        val originalBeerIndexedSite = assertBeerSiteLoaded()

        val pathWithGoodBeerZipFileButBrokenCoffeeZipFile = "/data/se-example-dir-7"
        seDirParser.parseFromClasspath(pathWithGoodBeerZipFileButBrokenCoffeeZipFile, {true});
        val currentBeerIndexedSite = assertBeerSiteLoaded()
        val currentCoffeeIndexedSite = assertCoffeeSiteLoaded()

        assertThat(currentBeerIndexedSite.indexedSiteId).isNotEqualTo(originalBeerIndexedSite.indexedSiteId)
        assertThat(currentCoffeeIndexedSite.indexedSiteId).isEqualTo(originalCoffeeIndexedSite.indexedSiteId)

        assertThat(indexes.indexedSiteIndex.searchByTerm("tinyName", "coffeeme")).hasSize(2)
        val coffeeIndexedSiteWithErrors = assertCoffeeSiteLoadedWithErrorMatching("Found non 'row' child element within \\[posts\\] with name \\[NotARow\\] child number \\[2\\] in file \\[Posts.xml\\] whilst parsing archive \\[[^\\]]*coffee.meta.stackexchange.com.7z\\]")
    }

    private fun assertBeerAndCoffeeSitesLoaded() {
        assertBeerSiteLoaded()
        assertCoffeeSiteLoaded()
    }

    private fun assertCoffeeSiteLoaded(): IndexedSite {
        val coffeeIndexedSite = indexes.indexedSiteIndex.getByTerms(mapOf("tinyName" to "coffeeme", "success" to "true"))!!
        val coffeeAssertions = CoffeeSiteAssertions(coffeeIndexedSite.indexedSiteId)
        coffeeAssertions.assertHasAllQuestions(indexes.questionIndex.getAll())
        return coffeeIndexedSite
    }

    private fun assertBeerSiteLoaded(): IndexedSite {
        val beerIndexedSite = indexes.indexedSiteIndex.getByTerms(mapOf("tinyName" to "beerme", "success" to "true"))!!
        val beerAssertions = BeerSiteAssertions(beerIndexedSite.indexedSiteId)
        beerAssertions.assertHasAllQuestions(indexes.questionIndex.getAll())
        return beerIndexedSite
    }

    private fun assertCoffeeSiteLoadedWithErrorMatching(message: String): IndexedSite {
        val coffeeIndexedSite = indexes.indexedSiteIndex.getByTerms(mapOf("tinyName" to "coffeeme", "success" to "false"))!!
        assertThat(coffeeIndexedSite.success).isFalse()
        assertThat(coffeeIndexedSite.errorMessage).matches(message)
        return coffeeIndexedSite
    }
}
