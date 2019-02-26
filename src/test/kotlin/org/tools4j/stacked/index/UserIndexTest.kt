package org.tools4j.stacked.index

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UserIndexTest {
    private lateinit var userIndex: UserIndex

    @BeforeEach
    fun setup(){
        userIndex = createUserIndex()
    }

    @Test
    fun testGetUserById() {
        assertUser1(userIndex.getById("1")!!);
        assertUser2(userIndex.getById("2")!!);
        assertUser3(userIndex.getById("3")!!);
        assertUser4(userIndex.getById("4")!!);
        assertUser5(userIndex.getById("5")!!);
        assertUser6(userIndex.getById("6")!!);
    }
}