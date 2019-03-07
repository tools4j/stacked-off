package org.tools4j.stacked.index

import java.io.File
import java.io.FileInputStream
import java.lang.IllegalStateException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

private val SITES_XML_FILE_NAME = "Sites.xml"

data class SeDir(val path: String) {
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
            } else if(!child.name.equals(SITES_XML_FILE_NAME)) {
                println("Unrecognized file in dump dir [${child.name}]")
            }
        }
        if(zipFiles.isEmpty()){
            throw IllegalStateException("Could not find any zip files at given path [${parentDir.absolutePath}], " +
                    "please ensure that there is at least one zip file in this directory, and that " +
                    "zip files have one of the following extensions ${getZipFileExtensions()}")
        }
        return zipFiles
    }

    private fun getSiteXmlFile(parentDir: File): File {
        var sitesXmlFile: File? = null
        parentDir.listFiles().forEach { child ->
            if (child.name.equals(SITES_XML_FILE_NAME, true)) {
                if (!child.isFile) {
                    throw IllegalStateException("Found $SITES_XML_FILE_NAME but it is " +
                            "not a file! [${child.absolutePath}]")
                }
                sitesXmlFile = child
            }
        }
        if(sitesXmlFile == null){
            throw IllegalStateException("Could not find $SITES_XML_FILE_NAME file.  " +
                    "Please ensure that when you download the associated $SITES_XML_FILE_NAME " +
                    "file when you download Stack Exchange data dump files.")
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

    fun isZipFile(file: File): Boolean {
        return getZipFileExtensions().contains(file.extension)
    }

    fun getZipFileExtensions(): Set<String>{
        return linkedSetOf("7z", "zip")
    }
}

data class SeDirContents(val siteXmlFile: File, val zipFiles: Set<File>){
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
        return matchedZipFilesBySite.map { SeDirSite(it.key, ZipFiles(it.value)) }.toSet()
    }
}

data class SeDirSite(val site: SeSite, val zipFiles: ZipFiles)

data class ZipFiles(val zipFiles: Set<File>){
    fun parse(seZipFileParser: SeZipFileParser){
        getPathsInsideZipFiles().forEach { seZipFileParser.parse(it) }
    }

    fun getPathsInsideZipFiles(): List<Path>{
        return zipFiles.flatMap { getPathsInsideZipFile(it) }
    }

    private fun getPathsInsideZipFile(fromZip: File): List<Path> {
        return FileSystems
            .newFileSystem(fromZip.toURI(), emptyMap<String, String>())
            .rootDirectories
            .flatMap { root -> Files.walk(root).toList() }
    }
}