package org.tools4j.stacked.index

import java.io.File

class SeDirParser(
    private val zipFileParser: SeZipFileParser,
    private val indexes: Indexes
) {
    fun parseFromClasspath(pathOnClasspath: String, filter: (SeSite) -> Boolean, jobStatus: JobStatus = JobStatusImpl()) {
        val archiveFile = getFileOnClasspath(this.javaClass,pathOnClasspath)
        return parse(archiveFile.absolutePath, filter, jobStatus)
    }

    fun parse(dirPath: String, filter: (SeSite) -> Boolean, jobStatus: JobStatus = JobStatusImpl()) {
        try {
            jobStatus.addOperation("Parsing xml files in $dirPath")
            var nextIndexedSiteId = indexes.indexedSiteIndex.getHighestIndexedSiteId() + 1
            val dir = File(dirPath).absolutePath
            val dirContents = SeDir(dir).getContents()
            val seDirSites = dirContents.getSites()
            for (seDirSite in seDirSites) {
                parseSeSite(seDirSite, filter, nextIndexedSiteId++.toString(), jobStatus)
            }
        } finally {
            jobStatus.onComplete()
        }
    }

    private fun parseSeSite(
        seDirSite: SeDirSite,
        filter: (SeSite) -> Boolean,
        newIndexedSiteId: String,
        jobStatus: JobStatus = JobStatusImpl()
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
                zipFileParser.parse(newIndexedSiteId, zipFile.absolutePath, jobStatus)
            }
            jobStatus.addOperation("Finished parsing site ${seSite.urlDomain}")
            for (matchingExistingIndexedSite in matchingExistingIndexedSites) {
                jobStatus.addOperation("Purging old site ${matchingExistingIndexedSite.indexedSiteId}. ${matchingExistingIndexedSite.seSite.urlDomain}")
            }
            indexes.purgeSites(matchingExistingIndexedSites)
            indexes.indexedSiteIndex.addItem(indexingSite.finished(true, null))

        } catch (e: Exception) {
            println(e.message)
            val exceptionAsString = if(e is ExtractorException) e.message else ExceptionToString(e).toString()
            if (matchingExistingIndexedSites.isNotEmpty()) {
                jobStatus.addOperation("Error ocurred whilst parsing site ${seSite.urlDomain}, purging loaded data...")
                indexes.purgeSite(newIndexedSiteId)
                jobStatus.addOperation("Site purged.")
                jobStatus.addOperation("Exiting with error\n$exceptionAsString")
            }
            indexes.indexedSiteIndex.addItem(indexingSite.finished(false, exceptionAsString))
        }
    }
}