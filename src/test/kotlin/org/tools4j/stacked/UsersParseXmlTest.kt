package org.tools4j.stacked

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UsersParseXmlTest {
    @Test
    fun testParseUsers(){
        val users = Users.fromXmlOnClasspath("/data/example/Users.xml").users!!
        assertThat(users).hasSize(6)
        UserTestUtils.assertHasAllUsers(users)
    }
}