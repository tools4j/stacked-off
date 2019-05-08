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
        assertSite1AndSite2Loaded(setup.indexes)
        setup.diContext.shutdown()
    }

    private fun assertSite1AndSite2Loaded(indexes: Indexes) {
        assertSite1Loaded(indexes)
        assertSite2Loaded(indexes)
    }

    private fun assertSite1Loaded(indexes: Indexes) {
        val coffeeIndexedSite = indexes.indexedSiteIndex.getByQuery(TermQuery(Term("tinyName", "coffeeme")))!!
        val coffeeAssertions = CoffeeSiteAssertions(coffeeIndexedSite.indexedSiteId)
        coffeeAssertions.assertHasAllQuestions(indexes.questionIndex.getAll())
    }

    private fun assertSite2Loaded(indexes: Indexes) {
        val beerIndexedSite = indexes.indexedSiteIndex.getByQuery(TermQuery(Term("tinyName", "beerme")))!!
        val beerAssertions = BeerSiteAssertions(beerIndexedSite.indexedSiteId)
        beerAssertions.assertHasAllQuestions(indexes.questionIndex.getAll())
    }
}