package org.tools4j.stacked.index

import org.apache.lucene.index.Term
import org.apache.lucene.search.TermQuery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import java.io.File

internal class InstanceTest {
    @BeforeEach
    fun setup(){
        assertThat(File("./data").deleteRecursively()).isTrue()
    }

    @Test
    fun testSetupWithLoadingOfDirectory(){
        val setup = Instance()
        val path = File(this.javaClass.getResource("/data/se-example-dir-6").toURI())
        setup.seDirParser.parse(path.absolutePath, {true});
        assertSite1AndSite2Loaded(setup.stagingIndexes)
        setup.diContext.shutdown()
    }

    private fun assertSite1AndSite2Loaded(stagingIndexes: StagingIndexes) {
        assertSite1Loaded(stagingIndexes)
        assertSite2Loaded(stagingIndexes)
    }

    private fun assertSite1Loaded(stagingIndexes: StagingIndexes) {
        val coffeeIndexedSite = stagingIndexes.indexedSiteIndex.getByQuery(TermQuery(Term("tinyName", "coffeeme")))!!
        val coffeeAssertions = CoffeeSiteAssertions(coffeeIndexedSite.indexedSiteId)
        coffeeAssertions.assertHasAllRawPosts(stagingIndexes.postIndex.getAll())
        coffeeAssertions.assertHasAllComments(stagingIndexes.commentIndex.getAll())
        coffeeAssertions.assertHasAllUsers(stagingIndexes.userIndex.getAll())
    }

    private fun assertSite2Loaded(stagingIndexes: StagingIndexes) {
        val beerIndexedSite = stagingIndexes.indexedSiteIndex.getByQuery(TermQuery(Term("tinyName", "beerme")))!!
        val beerAssertions = BeerSiteAssertions(beerIndexedSite.indexedSiteId)
        beerAssertions.assertHasAllRawPosts(stagingIndexes.postIndex.getAll())
        beerAssertions.assertHasAllComments(stagingIndexes.commentIndex.getAll())
        beerAssertions.assertHasAllUsers(stagingIndexes.userIndex.getAll())
    }
}