package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UsersParseXmlTest {
    @Test
    fun testParseUsers(){
        val users = ArrayList<User>()
        val xmlRowHandlerFactory = XmlRowHandlerFactory(
            listOf(
                UserXmlRowHandler(ToListHandler(users))
            )
        )
        val xmlRowParser = XmlFileParser("/data/example/Users.xml", xmlRowHandlerFactory)
        xmlRowParser.parse()
        
        assertThat(users).hasSize(6)
        assertHasAllUsers(users)
    }
}