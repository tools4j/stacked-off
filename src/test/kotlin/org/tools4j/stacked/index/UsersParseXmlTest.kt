package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UsersParseXmlTest {
    private val s1 = Site1Assertions()

    @Test
    fun testParseUsers(){
        val users = ArrayList<User>()
        val xmlRowHandlerFactory = XmlRowHandlerFactory(
            listOf(
                UserXmlRowHandler(ToListHandler(users))
            )
        )
        val xmlFileParser = XmlFileParser("/data/example/Users.xml", "1", xmlRowHandlerFactory)
        xmlFileParser.parse()
        
        assertThat(users).hasSize(6)
        s1.assertHasAllUsers(users)
    }
}