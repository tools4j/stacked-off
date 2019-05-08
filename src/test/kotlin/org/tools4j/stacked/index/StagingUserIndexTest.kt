package org.tools4j.stacked.index

import org.apache.lucene.index.Term
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class StagingUserIndexTest {
    private lateinit var stagingUserIndex: StagingUserIndex
    private val s1 = CoffeeStagingAssertions()

    @BeforeEach
    fun setup(){
        stagingUserIndex = coffeeSiteIndexUtils.createAndLoadUserIndex()
    }

    @Test
    fun testGetUserById() {
        s1.assertUser1(stagingUserIndex.getById("1")!!);
        s1.assertUser2(stagingUserIndex.getById("2")!!);
        s1.assertUser3(stagingUserIndex.getById("3")!!);
        s1.assertUser4(stagingUserIndex.getById("4")!!);
        s1.assertUser5(stagingUserIndex.getById("5")!!);
        s1.assertUser6(stagingUserIndex.getById("6")!!);
    }

    @Test
    fun testPurgeBySiteId() {
        assertThat(stagingUserIndex.getAll()).hasSize(6)
        stagingUserIndex.purge()
        assertThat(stagingUserIndex.getAll()).isEmpty()
    }
}