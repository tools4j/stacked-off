package org.tools4j.stacked.index

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UserIndexTest {
    private lateinit var userIndex: UserIndex
    private val s1 = Site1Assertions()

    @BeforeEach
    fun setup(){
        userIndex = createAndLoadUserIndex()
    }

    @Test
    fun testGetUserById() {
        s1.assertUser1(userIndex.getByUid("$SITE_1.1")!!);
        s1.assertUser2(userIndex.getByUid("$SITE_1.2")!!);
        s1.assertUser3(userIndex.getByUid("$SITE_1.3")!!);
        s1.assertUser4(userIndex.getByUid("$SITE_1.4")!!);
        s1.assertUser5(userIndex.getByUid("$SITE_1.5")!!);
        s1.assertUser6(userIndex.getByUid("$SITE_1.6")!!);
    }
}