package org.tools4j.stacked.index

import mu.KLogging

class Instance {
    companion object: KLogging()

    val diContext: DiContext by lazy {
        DiContext()
    }

    init {
        logger.info { "initializing context...." }
        diContext.init()
    }

    val seDirParser: SeDirParser by lazy {
        SeDirParser(seZipFileParser, {indexes.indexedSiteIndex.getHighestIndexedSiteId()}, parseSiteListener)
    }

    private val parseSiteListener: ParseSiteListener by lazy {
        SeDirParserListener(indexes)
    }

    private val seSeFileInZipParserProvider: SeFileInZipParserProvider by lazy {
        SeFileInZipParserProviderImpl(xmlRowHandlersByFileName);
    }

    private val seZipFileParser: SeZipFileParser by lazy {
        SeZipFileParser(seSeFileInZipParserProvider)
    }

    private val xmlRowHandlersByFileName: Map<String, XmlRowHandler<*>> by lazy {
        mapOf(
            "Posts.xml" to PostXmlRowHandler(stagingPostIndex.getItemHandler()),
            "Users.xml" to UserXmlRowHandler(stagingUserIndex.getItemHandler()),
            "Comments.xml" to CommentXmlRowHandler(stagingCommentIndex.getItemHandler()))
    }

    private val indexFactory: IndexFactory by lazy {
        FileIndexFactory(diContext.getIndexParentDir()!!)
    }

    val stagingIndexes: StagingIndexes by lazy {
        StagingIndexes(stagingPostIndex, stagingCommentIndex, stagingUserIndex)
    }

    private val stagingPostIndex: StagingPostIndex by lazy {
        diContext.addShutdownable(diContext.addInit(StagingPostIndex(indexFactory)))
    }

    private val stagingCommentIndex: StagingCommentIndex by lazy {
        diContext.addShutdownable(diContext.addInit(StagingCommentIndex(indexFactory)))
    }

    private val stagingUserIndex: StagingUserIndex by lazy {
        diContext.addShutdownable(diContext.addInit(StagingUserIndex(indexFactory)))
    }

    private val indexedSiteIndex: IndexedSiteIndex by lazy {
        diContext.addShutdownable(diContext.addInit(IndexedSiteIndex(indexFactory)))
    }

    val questionIndex: QuestionIndex by lazy {
        diContext.addShutdownable(diContext.addInit(QuestionIndex(indexFactory, indexedSiteIndex)))
    }

    val indexes: Indexes by lazy {
        Indexes(indexedSiteIndex, questionIndex, stagingIndexes)
    }
}
