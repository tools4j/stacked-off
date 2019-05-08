package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UsersParseXmlTest {
    private val s1 = CoffeeStagingAssertions()

    @Test
    fun testParseUsers(){
        val users = ArrayList<StagingUser>()
        val xmlFileParser = XmlFileParser(Dummy().javaClass.getResourceAsStream("/data/coffee/Users.xml"),
            UserXmlRowHandler(ToListHandler(users)))
        xmlFileParser.parse()
        
        assertThat(users).hasSize(6)
        s1.assertHasAllUsers(users)
    }
}