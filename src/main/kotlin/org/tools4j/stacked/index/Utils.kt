package org.tools4j.stacked.index

import java.io.File

fun getFileOnClasspath(contextClass: Class<*>, pathOnClasspath: String): File {
    val resource = contextClass.getResource(pathOnClasspath)
    if(resource == null){
        throw IllegalArgumentException("No resource found on classpath at: $pathOnClasspath")
    }
    return File(resource.toURI())
}