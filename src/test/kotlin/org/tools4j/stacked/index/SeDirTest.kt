package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import java.util.*

internal class SeDirTest {
    @Test
    fun getContentsOfDirWhichDoesNotExist() {
        val dir = "1234567890"
        assertThatCode({SeDir(dir).getContents()})
            .hasMessageMatching("Cannot find dir specified by path \\[$dir\\] relating to absolute path \\[.*?$dir\\]")
    }

    @Test
    fun getContentsOfDirWhichIsActuallyAFile() {
        val dir = getFileOnClasspath("/data/se-example-dir-4/Sites.xml").absolutePath
        assertThatCode({SeDir(dir).getContents()})
            .hasMessageMatching("Path specified is not a directory \\[.*?data.se-example-dir-4.Sites.xml\\] relating to " +
                    "absolute path \\[.*?data.se-example-dir-4.Sites.xml\\]")
    }

    @Test
    fun getContentsOfEmptyDirErrorsWithNoSiteXmlFound() {
        val dir = getFileOnClasspath("/data/se-example-dir-1").absolutePath
        assertThatCode({SeDir(dir).getContents()})
            .hasMessageContaining("Could not find Sites.xml file")
    }

    @Test
    fun getContentsOfDirWhichJustContainsSitesXmlFile() {
        val dir = getFileOnClasspath("/data/se-example-dir-2").absolutePath
        assertThatCode({SeDir(dir).getContents()})
            .hasMessageMatching("Could not find any zip files at given path \\[[^\\]]*\\]," +
                    " please ensure that there is at least one zip file in this directory, " +
                    "and that zip files have one of the following extensions " +
                    "\\[7z, zip]")
    }

    @Test
    fun getContentsOfDirWhichContainsSitesXmlThatIsADirectory() {
        val dir = getFileOnClasspath("/data/se-example-dir-3").absolutePath
        assertThatCode({SeDir(dir).getContents()})
            .hasMessageMatching("Found Sites\\.xml but it is not a file! \\[.*?Sites.xml\\]")    }

    @Test
    fun getContentsOfDirWhichContains7zAndZipFiles() {
        val dir = getFileOnClasspath("/data/se-example-dir-4").absolutePath
        val dirContents = SeDir(dir).getContents()

        assertThat(dirContents.siteXmlFile.absolutePath.replace("\\", "/"))
            .endsWith("/data/se-example-dir-4/Sites.xml")
        assertThat(dirContents.zipFiles.first{ it.name == "beer.meta.stackexchange.com.7z" }.absolutePath.replace("\\", "/"))
            .endsWith("/data/se-example-dir-4/beer.meta.stackexchange.com.7z")
        assertThat(dirContents.zipFiles.first{ it.name == "coffee.meta.stackexchange.com.zip" }.absolutePath.replace("\\", "/"))
            .endsWith("/data/se-example-dir-4/coffee.meta.stackexchange.com.zip")
    }
}