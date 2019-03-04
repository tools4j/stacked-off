package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UsersParseXmlTest {
    private val s1 = Site1Assertions()

    @Test
    fun testParseUsers(){
        val users = ArrayList<User>()
        val xmlFileParser = XmlFileParser("/data/coffee/Users.xml",
            "1",
            {UserXmlRowHandler({ToListHandler(users)})})
        xmlFileParser.parse()
        
        assertThat(users).hasSize(6)
        s1.assertHasAllUsers(users)
    }
}