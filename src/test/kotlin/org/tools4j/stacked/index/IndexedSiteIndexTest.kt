package org.tools4j.stacked.index

import org.apache.lucene.index.Term
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class IndexedSiteIndexTest {
    private lateinit var indexedSiteIndex: IndexedSiteIndex

    @BeforeEach
    fun setup(){
        indexedSiteIndex = createAndLoadIndexedSiteIndex()
    }

    @Test
    fun testGetAll() {
        val indexedSites = indexedSiteIndex.getAll()
        assertThat(indexedSites).hasSize(2)
        assertHasIndexedSite1(indexedSites)
        assertHasIndexedSite2(indexedSites)
    }

    @Test
    fun testAddIndividuallyThenGetAll() {
        indexedSiteIndex = createIndexedSiteIndex()

        val xmlRowParser = SeSiteXmlFileParser(Dummy().javaClass.getResourceAsStream("/data/Sites.xml"));
        val sites = xmlRowParser.parse()

        val beerIndexedSite = IndexedSiteImpl(
            "1",
            "2019-02-25T10:00:00",
            true,
            null,
            sites.first { it.tinyName == "beerme" })

        indexedSiteIndex.addItem(beerIndexedSite)

        val indexedSites = indexedSiteIndex.getAll()
        assertThat(indexedSites).hasSize(1)
    }

    @Test
    fun testGetByUid() {
        assertIsIndexedSite1(indexedSiteIndex.getByUid("1")!!)
        assertIsIndexedSite2(indexedSiteIndex.getByUid("2")!!)
    }

    @Test
    fun testPurgeBySiteId() {
        assertThat(indexedSiteIndex.searchByTerm(Term("indexedSiteId", SITE_1))).hasSize(1)
        indexedSiteIndex.purgeSite(SITE_1)
        assertThat(indexedSiteIndex.searchByTerm(Term("indexedSiteId", SITE_1))).isEmpty()
    }

    @Test
    fun testQueryWithMultipleCriteria(){
        assertIsIndexedSite1(indexedSiteIndex.getByTerms(mapOf("tinyName" to "beerme", "success" to "true"))!!)
    }


}