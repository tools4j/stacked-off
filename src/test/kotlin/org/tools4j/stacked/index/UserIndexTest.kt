package org.tools4j.stacked.index

import org.apache.lucene.index.Term
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UserIndexTest {
    private lateinit var userIndex: UserIndex
    private val s1 = CoffeeSiteAssertions()

    @BeforeEach
    fun setup(){
        userIndex = createAndLoadUserIndex()
    }

    @Test
    fun testGetUserById() {
        s1.assertUser1(userIndex.getByUid("${s1.indexedSiteId}.1")!!);
        s1.assertUser2(userIndex.getByUid("${s1.indexedSiteId}.2")!!);
        s1.assertUser3(userIndex.getByUid("${s1.indexedSiteId}.3")!!);
        s1.assertUser4(userIndex.getByUid("${s1.indexedSiteId}.4")!!);
        s1.assertUser5(userIndex.getByUid("${s1.indexedSiteId}.5")!!);
        s1.assertUser6(userIndex.getByUid("${s1.indexedSiteId}.6")!!);
    }

    @Test
    fun testPurgeBySiteId() {
        assertThat(userIndex.searchByTerm(Term("indexedSiteId", s1.indexedSiteId))).hasSize(6)
        userIndex.purgeSite(s1.indexedSiteId)
        assertThat(userIndex.searchByTerm(Term("indexedSiteId", s1.indexedSiteId))).isEmpty()
    }
}