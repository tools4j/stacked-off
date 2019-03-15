package org.tools4j.stacked.index

import net.sf.sevenzipjbinding.*
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import java.io.*

interface SeHandlerProvider {
    fun fileInZipParser(pathInArchive: String): FileInZipParser?
}

class SeZipFileParser(val handlerProvider: SeHandlerProvider) {
    fun parse(archiveFile: String) {
        println("Parsing $archiveFile")
        RandomAccessFile(archiveFile, "r").use { randomAccessFile ->
            SevenZip.openInArchive(
                null, // autodetect archive type
                RandomAccessFileInStream(randomAccessFile)
            ).use { archive ->
                val archiveIndicesToParse = IntArray(archive.numberOfItems)
                for (i in archiveIndicesToParse.indices) {
                    archiveIndicesToParse[i] = i
                }
                val extractCallback = ExtractCallback(archive, handlerProvider)

                archive.extract(
                    archiveIndicesToParse,
                    false, // Non-test mode
                    extractCallback
                )
            }
        }
    }
}

class ExtractCallback(
    private val archive: IInArchive,
    private val handlerProvider: SeHandlerProvider) : IArchiveExtractCallback {

    private var index = -1
    private var fileInZipParser: FileInZipParser? = null
    @Volatile private var extractedFileInZipSize = 0
    private var totalFileInZipSize: Int = 0;

    override fun getStream(
        index: Int,
        extractAskMode: ExtractAskMode
    ): ISequentialOutStream? {
        this.index = index
        val skipExtraction = archive.getProperty(index, PropID.IS_FOLDER) as Boolean
        if (skipExtraction || extractAskMode != ExtractAskMode.EXTRACT){
            return null
        }
        val pathInArchive = archive.getProperty(index, PropID.PATH).toString()
        totalFileInZipSize = Integer.parseInt(archive.getProperty(index, PropID.SIZE).toString())
        fileInZipParser = handlerProvider.fileInZipParser(pathInArchive)?: return null
        fileInZipParser!!.start()
        extractedFileInZipSize = 0

        return ISequentialOutStream { data ->
            fileInZipParser!!.outputStreamToWriteTo.write(data)
            extractedFileInZipSize += data.size
            println("$extractedFileInZipSize/$totalFileInZipSize bytes extracted...")
            data.size // Return amount of processed data
        }
    }

    override fun setOperationResult(extractOperationResult: ExtractOperationResult) {
        if (extractOperationResult != ExtractOperationResult.OK) {
            throw IllegalStateException("Extraction error")
        }
        if(index < 0 || fileInZipParser == null) return
        println("Completed: ${fileInZipParser!!.fileName}")
        fileInZipParser!!.close()
    }

    override fun setCompleted(completeValue: Long) {}
    override fun prepareOperation(extractAskMode: ExtractAskMode?) {}
    override fun setTotal(total: Long) {}
}

class SeHandlerProviderImpl(
    val indexedSiteId: String,
    val rowHandlers: Map<String, () -> XmlRowHandler<*>>) : SeHandlerProvider {

    override fun fileInZipParser(pathInArchive: String): FileInZipParser? {
        if(!rowHandlers.containsKey(pathInArchive)){
            return null
        }
        val xmlRowHandlerProvider = rowHandlers.getValue(pathInArchive)
        val pipedInputStream = PipedInputStream()
        val pipedOutputStream = PipedOutputStream(pipedInputStream)
        val xmlFileParser = XmlFileParser(pipedInputStream, indexedSiteId, xmlRowHandlerProvider )

        return FileInZipParser(pathInArchive, xmlFileParser, pipedOutputStream)
    }
}

class FileInZipParser(
    val fileName: String,
    val xmlFileParser: XmlFileParser,
    val outputStreamToWriteTo: OutputStream) {

    lateinit var thread: Thread

    fun start() {
        thread = Thread {
            xmlFileParser.parse()
        }
        thread.start()
    }

    fun close(){
        outputStreamToWriteTo.flush()
        outputStreamToWriteTo.close()
        thread.join()
    }
}