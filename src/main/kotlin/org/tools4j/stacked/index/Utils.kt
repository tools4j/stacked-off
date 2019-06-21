package org.tools4j.stacked.index

import mu.KotlinLogging
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

val BYTES_IN_KB = 1024
val BYTES_IN_MB = 1024*1024

val logger = KotlinLogging.logger {}

fun getFileOnClasspath(contextClass: Class<*>, pathOnClasspath: String): File {
    val resource = contextClass.getResource(pathOnClasspath)
    if(resource == null){
        throw IllegalArgumentException("No resource found on classpath at: $pathOnClasspath")
    }
    return File(resource.toURI())
}

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

public fun getOrCreateStackedOffProperties(): Properties {
    val stackedPropertiesFile = getOrCreateStackedOffPropertiesFile()
    val fis = FileInputStream(stackedPropertiesFile)
    val properties = Properties()
    properties.load(fis)
    fis.close()
    return properties
}

public fun saveStackedOffUserProperties(properties: Properties) {
    val fos = FileOutputStream(getOrCreateStackedOffPropertiesFile())
    properties.store(fos, null)
    fos.close()
}

public fun getOrCreateStackedOffPropertiesFile(): File {
    val stackedSettingsDir = getOrCreateStackedOffSettingsDir()
    val stackedPropertiesFilePath = stackedSettingsDir.absolutePath + File.separator + "app.properties"
    val stackedPropertiesFile = File(stackedPropertiesFilePath)
    if (!stackedPropertiesFile.exists()) {
        logger.info { "Could not find stackedoff application properties at [${stackedPropertiesFile.absolutePath}] so creating..." }
        stackedPropertiesFile.writeText("# stackedoff application properties")
    }
    return stackedPropertiesFile
}

public fun getOrCreateStackedOffSettingsDir(): File {
    val userHomeDirPath = System.getProperty("user.home")
    if (userHomeDirPath == null) throw IllegalStateException("Cannot find user directory path using system property user.home")
    val userHomeDir = File(userHomeDirPath)
    if (!userHomeDir.exists()) throw IllegalStateException("Cannot find user directory at [${userHomeDir.absolutePath}]")
    logger.info { "Found userHomeDir at [${userHomeDir.absolutePath}]" }
    val stackedSettingsDirPath = userHomeDir.absolutePath + File.separator + ".stackedoff"
    val stackedSettingsDir = File(stackedSettingsDirPath)
    if (!stackedSettingsDir.exists()) {
        logger.info { "Could not find stackedoff directory at [${stackedSettingsDir.absolutePath}] so creating..." }
        stackedSettingsDir.mkdirs()
    }
    return stackedSettingsDir
}


