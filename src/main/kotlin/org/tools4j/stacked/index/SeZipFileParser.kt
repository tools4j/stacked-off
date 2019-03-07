package org.tools4j.stacked.index

import java.nio.file.Files
import java.nio.file.Path

class SeZipFileParser(
    val xmlRowHandlersByFilename: Map<String, () -> XmlRowHandler<*>>,
    val indexedSiteIdGenerator: IndexedSiteIdGenerator) {

    fun parse(path: Path) {
        println("Parsing: $path")
        val fileName = path.fileName.toString()
        if (xmlRowHandlersByFilename.containsKey(fileName)) {
            val xmlRowHandlerProvider = xmlRowHandlersByFilename[fileName]!!
            val xmlFileInputStream = Files.newInputStream(path)
            XmlFileParser(
                xmlFileInputStream,
                indexedSiteIdGenerator.getNext(),
                xmlRowHandlerProvider
            ).parse()
        }
    }
}