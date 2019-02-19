package org.tools4j.stacked

import org.junit.jupiter.api.Test

internal class UserIndexTest {
    @Test
    fun testGetUserById() {
        val userIndex = UserIndex(RamIndexFactory())
        userIndex.init()

        val userXmlRowHandler = UserXmlRowHandler(userIndex.getItemHandler())
        val xmlFileParser = XmlFileParser("/data/example/Users.xml", XmlRowHandlerFactory(listOf(userXmlRowHandler)))
        xmlFileParser.parse()

        UserTestUtils.assertUser1(userIndex.getById("1")!!);
        UserTestUtils.assertUser2(userIndex.getById("2")!!);
        UserTestUtils.assertUser3(userIndex.getById("3")!!);
        UserTestUtils.assertUser4(userIndex.getById("4")!!);
        UserTestUtils.assertUser5(userIndex.getById("5")!!);
        UserTestUtils.assertUser6(userIndex.getById("6")!!);
    }
}