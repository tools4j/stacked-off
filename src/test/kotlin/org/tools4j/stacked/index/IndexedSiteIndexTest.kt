package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class IndexedSiteIndexTest {
    private lateinit var indexedSiteIndex: IndexedSiteIndex
    private val s1 = Site1Assertions()

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
    fun testGetByUid() {
        assertIsIndexedSite1(indexedSiteIndex.getByUid("1")!!)
        assertIsIndexedSite2(indexedSiteIndex.getByUid("2")!!)
    }
}