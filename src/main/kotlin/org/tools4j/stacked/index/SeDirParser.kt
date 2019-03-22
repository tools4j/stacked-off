package org.tools4j.stacked.index

import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class SeDirParser(
    private val zipFileParser: SeZipFileParser,
    private val indexes: Indexes) {

    fun parse(dirPath: String, filter: (SeSite) -> Boolean) {
        var nextIndexedSiteId = indexes.indexedSiteIndex.getHighestIndexedSiteId() + 1
        val dir = File(dirPath).absolutePath
        val dirContents = SeDir(dir).getContents()
        val seDirSites = dirContents.getSites()
        for (seDirSite in seDirSites) {
            parseSeSite(seDirSite, filter, nextIndexedSiteId++.toString())
        }
    }

    private fun parseSeSite(
        seDirSite: SeDirSite,
        filter: (SeSite) -> Boolean,
        newIndexedSiteId: String
    ) {
        val seSite = seDirSite.site
        if (!filter(seSite)) return
        val matchingExistingIndexedSites = indexes.indexedSiteIndex.getMatching(seSite)
        val indexingSite = IndexingSiteImpl(
            newIndexedSiteId,
            "2019-10-11T10:00:00",
            seSite
        )
        try {
            for (zipFile in seDirSite.zipFiles) {
                zipFileParser.parse(newIndexedSiteId, zipFile.absolutePath)
            }
            println("Finished parsing site $seSite")
            indexes.purgeSites(matchingExistingIndexedSites)
            indexes.indexedSiteIndex.addItem(indexingSite.finished(true, null))

        } catch (e: Exception) {
            println("Error parsing site $seSite, ${e.message}")
            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
            val exceptionAsString = sw.toString()
            println(exceptionAsString)
            if (matchingExistingIndexedSites.isNotEmpty()) {
                indexes.purgeSite(newIndexedSiteId)
            }
            indexes.indexedSiteIndex.addItem(indexingSite.finished(false, exceptionAsString))
        }
    }
}