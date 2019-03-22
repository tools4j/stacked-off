package org.tools4j.stacked.index

import org.apache.lucene.index.Term
import org.apache.lucene.search.TermQuery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import java.io.File

internal class SetupTest {
    @BeforeEach
    fun setup(){
        assertThat(File("./data").deleteRecursively()).isTrue()
    }

    @Test
    fun testSetupWithoutErrors(){
        val setup = Setup()
        val path = File(this.javaClass.getResource("/data/se-example-dir-6").toURI())
        setup.seDirParser.parse(path.absolutePath, {true});
        assertSite1AndSite2Loaded(setup.indexes)
        setup.diContext.shutdown()
    }

    private fun assertSite1AndSite2Loaded(indexes: Indexes) {
        println(indexes.indexedSiteIndex.getAll())
        val coffeeIndexedSite = indexes.indexedSiteIndex.getByTermQuery(TermQuery(Term("tinyName", "coffeeme")))!!
        val beerIndexedSite = indexes.indexedSiteIndex.getByTermQuery(TermQuery(Term("tinyName", "beerme")))!!

        val coffeeAssertions = CoffeeSiteAssertions(coffeeIndexedSite.indexedSiteId)
        val beerAssertions = BeerSiteAssertions(beerIndexedSite.indexedSiteId)

        coffeeAssertions.assertHasAllRawPosts(indexes.postIndex.getAll())
        coffeeAssertions.assertHasAllComments(indexes.commentIndex.getAll())
        coffeeAssertions.assertHasAllUsers(indexes.userIndex.getAll())

        beerAssertions.assertHasAllRawPosts(indexes.postIndex.getAll())
        beerAssertions.assertHasAllComments(indexes.commentIndex.getAll())
        beerAssertions.assertHasAllUsers(indexes.userIndex.getAll())
    }


    @Disabled
    @Test
    fun testDirParser() {
        val setup = Setup()
        setup.seDirParser.parse(
            "C:\\Users\\ben\\Downloads\\stackexchange",
            {seSite: SeSite ->
                seSite.url.endsWith("unix.stackexchange.com")
            });
    }
}