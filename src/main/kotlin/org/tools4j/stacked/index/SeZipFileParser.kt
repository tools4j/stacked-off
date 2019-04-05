package org.tools4j.stacked.index

import net.sf.sevenzipjbinding.*
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import java.io.*
import java.util.concurrent.Executors
import java.util.concurrent.Future

class SeZipFileParser(private val seFileInZipParserProvider: SeFileInZipParserProvider) {
    fun parse(
        indexedSiteId: String,
        archiveFile: String,
        jobStatus: JobStatus = JobStatusImpl()
    ) {
        println("Parsing $archiveFile")
        RandomAccessFile(archiveFile, "r").use { randomAccessFile ->
            try {
                SevenZip.openInArchive(
                    null, // autodetect archive type
                    RandomAccessFileInStream(randomAccessFile)
                ).use { archive ->
                    val archiveIndicesToParse = IntArray(archive.numberOfItems)
                    for (i in archiveIndicesToParse.indices) {
                        archiveIndicesToParse[i] = i
                    }
                    val extractCallback =
                        ExtractCallback(
                            indexedSiteId,
                            archiveFile,
                            archive,
                            seFileInZipParserProvider,
                            jobStatus)

                    var outerException: Exception? = null
                    try {
                        archive.extract(
                            archiveIndicesToParse,
                            false, // Non-test mode
                            extractCallback
                        )
                    } catch (e: Exception) {
                        outerException = e
                    }
                    if (extractCallback.exceptionDuringParsing != null) {
                        throw extractCallback.exceptionDuringParsing!!
                    } else if (outerException != null) {
                        throw UnknownExtractorException(archiveFile, outerException)
                    }
                }
            } catch (e: ExtractorException){
                throw e
            } catch (e: Exception){
                throw UnknownExtractorException(archiveFile, e)
            }
        }
    }
}

class ExtractCallback(
    private val indexedSiteId: String,
    private val archiveFile: String,
    private val archive: IInArchive,
    private val seFileInZipParserProvider: SeFileInZipParserProvider,
    private val jobStatus: JobStatus = JobStatusImpl()

) : IArchiveExtractCallback {
    private var index = -1
    private var fileInZipParser: FileInZipParser? = null
    private var totalFileInZipSize: Long = 0;
    @Volatile var parsingFuture: Future<*>? = null
    @Volatile lateinit var pathInArchive: String
    @Volatile private var extractedFileInZipSize = 0
    @Volatile var exceptionDuringParsing: Exception? = null

    override fun getStream(
        index: Int,
        extractAskMode: ExtractAskMode
    ): ISequentialOutStream? {
        if(exceptionDuringParsing != null) throw exceptionDuringParsing!!
        this.index = index
        val skipExtraction = archive.getProperty(index, PropID.IS_FOLDER) as Boolean
        if (skipExtraction || extractAskMode != ExtractAskMode.EXTRACT){
            return null
        }
        pathInArchive = archive.getProperty(index, PropID.PATH).toString()
        println("Extractor calling getStream() for: $pathInArchive")
        jobStatus.addOperation("Parsing $pathInArchive from $archiveFile...")
        totalFileInZipSize = archive.getProperty(index, PropID.SIZE).toString().toLong()
        fileInZipParser = seFileInZipParserProvider.getFileInZipParser(indexedSiteId, pathInArchive)
        if(fileInZipParser == null){
            return null
        }
        parsingFuture = Executors.newSingleThreadExecutor().submit({
            try {
                if(exceptionDuringParsing != null) throw exceptionDuringParsing!!
                fileInZipParser!!.start()

            } catch (e: FileInZipParserException) {
                e.printStackTrace()
                exceptionDuringParsing = ExtractorException(archiveFile, e)
                throw exceptionDuringParsing!!
            } catch (e: Exception) {
                e.printStackTrace()
                exceptionDuringParsing = UnknownExtractorException(archiveFile, e)
                throw exceptionDuringParsing!!
            }
        })

        extractedFileInZipSize = 0

        return ISequentialOutStream { data ->
            fileInZipParser!!.outputStreamToWriteTo.write(data)
            extractedFileInZipSize += data.size
            jobStatus.currentOperationProgress = toProgress(totalFileInZipSize, extractedFileInZipSize.toLong())
            data.size // Return amount of processed data
        }
    }

    override fun setOperationResult(extractOperationResult: ExtractOperationResult) {
        if (extractOperationResult != ExtractOperationResult.OK) {
            throw IllegalStateException("Extraction error")
        }
        if(index < 0 || fileInZipParser == null) return
        println("Completed extracting [${fileInZipParser!!.fileName}], from archive [$archiveFile] with indexedSiteId [$indexedSiteId]")
        /*
        At this point we know that writing to the output stream has finished.
        Therefore we need to close the output stream.  This will cause a -1
        to be written to the output stream.  The reader (in the future) might
        not have finished reading from the inputStream.  So we need to wait
        for it to finish.  Hence the 'get'
         */
        fileInZipParser!!.flushAndClose()
        parsingFuture?.get()
    }

    override fun setCompleted(completeValue: Long) {}
    override fun prepareOperation(extractAskMode: ExtractAskMode?) {}
    override fun setTotal(total: Long) {}
}

class UnknownExtractorException(val archiveFile: String, cause: Throwable): Exception(cause){
    override val message: String?
        get() = "Error occurred whilst parsing file [$archiveFile] ${cause?.message}"
}

class ExtractorException(val archiveFile: String, val fileInZipParserException: FileInZipParserException): Exception(){
    override val message: String?
        get() = fileInZipParserException.message + " whilst parsing archive [$archiveFile]"
}

interface SeFileInZipParserProvider {
    fun getFileInZipParser(indexedSiteId: String, pathInArchive: String): FileInZipParser?;
}

class SeFileInZipParserProviderImpl (
    val rowHandlers: Map<String, () -> XmlRowHandler<*>>) : SeFileInZipParserProvider {

    override fun getFileInZipParser(indexedSiteId: String, pathInArchive: String): FileInZipParser? {
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

    fun start() {
        try {
            xmlFileParser.parse()
        } catch(e: XmlFileParserException){
            throw FileInZipParserException(fileName, e)
        }
    }

    fun flushAndClose() {
        outputStreamToWriteTo.flush()
        outputStreamToWriteTo.close()
    }
}

class FileInZipParserException(
    val fileName: String,
    val xmlFileParserException: XmlFileParserException): Exception(xmlFileParserException){

    override val message: String?
        get() = xmlFileParserException.message + " in file [$fileName]"
}
