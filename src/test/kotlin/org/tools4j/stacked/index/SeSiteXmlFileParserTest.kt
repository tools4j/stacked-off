package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SeSiteXmlFileParserTest {
    @Test
    fun testLoadFromXml() {
        val posts = ArrayList<RawPost>()
        val xmlRowParser = SeSiteXmlFileParser(this.javaClass.getResourceAsStream("/data/Sites.xml"));
        val sites = xmlRowParser.parse()
        assertThat(sites).hasSize(3)
        assertHasSeSite1(sites);
        assertHasSeSite2(sites);
        assertHasSeSite3(sites);
    }
}