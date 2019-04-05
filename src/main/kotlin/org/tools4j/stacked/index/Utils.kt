package org.tools4j.stacked.index

import java.io.File

fun getFileOnClasspath(contextClass: Class<*>, pathOnClasspath: String): File {
    val resource = contextClass.getResource(pathOnClasspath)
    if(resource == null){
        throw IllegalArgumentException("No resource found on classpath at: $pathOnClasspath")
    }
    return File(resource.toURI())
}

val BYTES_IN_KB = 1024
val BYTES_IN_MB = 1024*1024

fun toProgress(totalBytes: Long, bytesRead: Long): String{
    if(totalBytes < (5* BYTES_IN_MB)){
        val totalBytesInKb = formatProgressDouble(totalBytes.toDouble() / BYTES_IN_KB.toDouble())
        val bytesReadInKb = formatProgressDouble(bytesRead.toDouble() / BYTES_IN_KB.toDouble())
        return "$bytesReadInKb / $totalBytesInKb KB"
    } else {
        val totalBytesInMb = formatProgressDouble(totalBytes.toDouble() / BYTES_IN_MB.toDouble())
        val bytesReadInMb = formatProgressDouble(bytesRead.toDouble() / BYTES_IN_MB.toDouble())
        return "$bytesReadInMb / $totalBytesInMb MB"
    }
}

fun formatProgressDouble(number: Double): String {
    if(number >= 10){
        return "%,d".format(number.toInt())
    } else {
        return "%.1f".format(number)
    }
}