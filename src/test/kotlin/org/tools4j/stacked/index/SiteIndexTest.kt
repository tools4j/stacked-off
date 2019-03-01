package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SiteIndexTest {
    private lateinit var siteIndex: SiteIndex

    @BeforeEach
    fun setup(){
        siteIndex = createSiteIndex()
    }

    @Test
    fun testQueryAllSitesFromIndex() {
        val results = siteIndex.getAll()
        assertThat(results).hasSize(3)
        assertHasSite1(results);
        assertHasSite2(results);
        assertHasSite3(results);
    }

    @Test
    fun testGetById() {
        val result = siteIndex.getByUid("1")!!
        assertIsSite1(result);
    }

    @Test
    fun testSearch() {
        val results = siteIndex.search("serverfault")!!
        assertThat(results).hasSize(1)
        assertHasSite3(results);
    }
}