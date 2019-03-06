package org.tools4j.stacked.index

import java.io.File
import java.io.FileInputStream
import java.lang.IllegalStateException

class SeDir(val path: String) {
    fun getContents(): SeDirContents {
        val pathDir = File(path)
        validateDumpPathDir(pathDir)
        val sitesXmlFile: File = getSiteXmlFile(pathDir)
        val zipFiles = getZipFiles(pathDir)
        return SeDirContents(sitesXmlFile, zipFiles)
    }

    private fun getZipFiles(parentDir: File): LinkedHashSet<File> {
        val zipFiles = LinkedHashSet<File>()
        parentDir.listFiles().forEach { child ->
            if (isZipFile(child)) {
                zipFiles.add(child)
            } else {
                println("Unrecognized file in dump dir [${child.name}]")
            }
        }
        return zipFiles
    }

    private fun getSiteXmlFile(parentDir: File): File {
        var sitesXmlFile: File? = null
        parentDir.listFiles().forEach { child ->
            if (child.name.equals("Sites.xml", true)) {
                if (child.isFile) {
                    throw IllegalStateException("Sites.xml file found is not a file! [${parentDir.absolutePath}]")
                }
                sitesXmlFile = child
            }
        }
        if(sitesXmlFile == null){
            throw IllegalStateException("Could not find Sites.xml file.  Please ensure this is downloaded.")
        }
        return sitesXmlFile!!
    }

    private fun validateDumpPathDir(pathDir: File) {
        if (!pathDir.exists()) {
            throw IllegalStateException(
                "Cannot find dir specified by path [$path] relating to absolute path" +
                        " [${pathDir.absolutePath}]"
            )
        }
        if (!pathDir.isDirectory) {
            throw IllegalStateException(
                "Path specified is not a directory [$path] relating to absolute path" +
                        " [${pathDir.absolutePath}]"
            )
        }
    }

    private fun isZipFile(file: File): Boolean {
        return file.name.endsWith(".7z")
                || file.name.endsWith(".zip")
                || file.name.endsWith(".gz")
    }
}

class SeDirContents(val siteXmlFile: File, val zipFiles: Set<File>){
    fun getSites(): Set<SeDirSite>{
        val matchedZipFilesBySite = LinkedHashMap<SeSite, MutableSet<File>>()
        val sitesByDomain = SeSiteXmlFileParser(FileInputStream(siteXmlFile)).parse().map { it.urlDomain to it }.toMap()
        for (zipFile in zipFiles) {
            val zipFilePrefix = zipFile.name.replace(Regex("(.*?)[\\-\\.$].*"), "$1")
            val matchingSite = sitesByDomain[zipFilePrefix]
            if(matchingSite != null){
                matchedZipFilesBySite.computeIfAbsent(matchingSite){LinkedHashSet()}
                matchedZipFilesBySite[matchingSite]!!.add(zipFile)
            }
        }
        return matchedZipFilesBySite.map { SeDirSite(it.key, it.value) }.toSet()
    }
}

class SeDirSite(val site: SeSite, val zipFiles: Set<File>){
    fun parse(){

    }
}