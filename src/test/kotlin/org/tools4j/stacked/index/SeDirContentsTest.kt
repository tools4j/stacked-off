package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SeDirContentsTest {
    @Test
    fun getSites_withSingleZipFilePerSite() {
        val dir = getFileOnClasspath("/data/se-example-dir-4").absolutePath
        val dirContents = SeDir(dir).getContents()
        val seDirSites = dirContents.getSites()
        assertThat(seDirSites).hasSize(2)

        val beerSeDirSite = seDirSites.first { it.site.seSiteId == "2" }
        assertThat(beerSeDirSite.site.tinyName).isEqualTo("beerme")
        assertThat(beerSeDirSite.zipFiles).hasSize(1)
        assertContainsZipFile(beerSeDirSite, "beer.meta.stackexchange.com.7z")

        val coffeeSeDirSite = seDirSites.first { it.site.seSiteId == "3" }
        assertThat(coffeeSeDirSite.site.tinyName).isEqualTo("coffeeme")
        assertThat(coffeeSeDirSite.zipFiles).hasSize(1)
        assertContainsZipFile(coffeeSeDirSite, "coffee.meta.stackexchange.com.zip")
    }

    @Test
    fun getSites_withMultipleZipFilePerSite() {
        val dir = getFileOnClasspath("/data/se-example-dir-6").absolutePath
        val dirContents = SeDir(dir).getContents()
        val seDirSites = dirContents.getSites()

        val beerSeDirSite = seDirSites.first { it.site.seSiteId == "2" }
        assertThat(beerSeDirSite.site.tinyName).isEqualTo("beerme")
        assertThat(beerSeDirSite.zipFiles).hasSize(4)
        assertContainsZipFile(beerSeDirSite, "beer.meta.stackexchange.com-Comments.7z")
        assertContainsZipFile(beerSeDirSite, "beer.meta.stackexchange.com-Posts.7z")
        assertContainsZipFile(beerSeDirSite, "beer.meta.stackexchange.com-Sites.7z")
        assertContainsZipFile(beerSeDirSite, "beer.meta.stackexchange.com-Users.7z")

        val coffeeSeDirSite = seDirSites.first { it.site.seSiteId == "3" }
        assertThat(coffeeSeDirSite.site.tinyName).isEqualTo("coffeeme")
        assertThat(coffeeSeDirSite.zipFiles).hasSize(1)
        assertContainsZipFile(coffeeSeDirSite, "coffee.meta.stackexchange.com.7z")
    }

    private fun assertContainsZipFile(seDirSite: SeDirSite, expectedZipFileName: String) {
        assertThat(seDirSite.zipFiles.any { it.name == expectedZipFileName })
            .withFailMessage("Could not find file $expectedZipFileName.  Zip files " +
                    "found ${seDirSite.zipFiles}")
            .isTrue()
    }
}