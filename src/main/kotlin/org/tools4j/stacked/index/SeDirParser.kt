package org.tools4j.stacked.index

import mu.KLogging
import java.io.File
import java.util.*

class SeDirParser(
    private val zipFileParser: SeZipFileParser,
    private val highestCurrentIndexedSiteIdProvider: () -> Long,
    private val parseSiteListener: ParseSiteListener
) {
    companion object: KLogging()

    fun parseFromClasspath(
        pathOnClasspath: String,
        filter: (SeSite) -> Boolean,
        jobStatus: JobStatus = JobStatusImpl()) {

        val archiveFile = getFileOnClasspath(this.javaClass,pathOnClasspath)
        parse(archiveFile.absolutePath, filter, jobStatus)
    }

    fun parse(
        dirPath: String,
        filter: (SeSite) -> Boolean,
        jobStatus: JobStatus = JobStatusImpl()) {

        try {
            jobStatus.addOperation("Parsing xml files in $dirPath")
            var nextIndexedSiteId = highestCurrentIndexedSiteIdProvider() + 1
            val dir = File(dirPath).absolutePath
            val dirContents = SeDir(dir).getContents()
            val seDirSites = dirContents.getSites().filter{filter(it.site)}
            for (seDirSite in seDirSites) {
                parseSeSite(seDirSite, nextIndexedSiteId++.toString(), jobStatus)
            }
        } finally {
            jobStatus.onComplete()
        }
    }

    private fun parseSeSite(
        seDirSite: SeDirSite,
        newIndexedSiteId: String,
        jobStatus: JobStatus = JobStatusImpl()
    ){
        val seSite = seDirSite.site
        val indexingSite = IndexingSiteImpl(
            newIndexedSiteId,
            Date().toString(),
            seSite
        )
        try {
            parseSiteListener.onStartParseSite(seSite)
            for (zipFile in seDirSite.zipFiles) {
                zipFileParser.parse(zipFile.absolutePath, jobStatus)
            }
            parseSiteListener.onFinishParseSite(indexingSite.finished(true, null), jobStatus)

        } catch (e: Exception) {
            logger.error{ e.message }
            val exceptionAsString = if(e is ExtractorException) e.message else ExceptionToString(e).toString()
            parseSiteListener.onFinishParseSite(indexingSite.finished(false, exceptionAsString), jobStatus)
        }
    }
}

interface ParseSiteListener {
    fun onStartParseSite(seSite: SeSite)
    fun onFinishParseSite(
        indexedSite: IndexedSite,
        jobStatus: JobStatus
    )
}