package org.tools4j.stacked.index

class Instance {
    val diContext: DiContext by lazy {
        DiContext()
    }

    val postService: PostService by lazy {
        PostService(postIndex, commentIndex, userIndex, indexedSiteIndex)
    }

    val seDirParser: SeDirParser by lazy {
        SeDirParser(seZipFileParser, indexes)
    }

    private val seSeFileInZipParserProvider: SeFileInZipParserProvider by lazy {
        SeFileInZipParserProviderImpl(xmlRowHandlersByFileName);
    }

    private val seZipFileParser: SeZipFileParser by lazy {
        SeZipFileParser(seSeFileInZipParserProvider)
    }

    private val xmlRowHandlersByFileName: Map<String, () -> XmlRowHandler<*>> by lazy {
        mapOf(
            "Posts.xml" to { PostXmlRowHandler { postIndex.getItemHandler() } },
            "Users.xml" to { UserXmlRowHandler { userIndex.getItemHandler() } },
            "Comments.xml" to { CommentXmlRowHandler { commentIndex.getItemHandler() } })
    }

    private val indexFactory: IndexFactory by lazy {
        FileIndexFactory(diContext.getIndexParentDir())
    }

    val indexes: Indexes by lazy {
        Indexes(indexedSiteIndex, postIndex, commentIndex, userIndex)
    }

    private val postIndex: PostIndex by lazy {
        diContext.addShutdownable(diContext.addInit(PostIndex(indexFactory)))
    }

    private val commentIndex: CommentIndex by lazy {
        diContext.addShutdownable(diContext.addInit(CommentIndex(indexFactory)))
    }

    private val userIndex: UserIndex by lazy {
        diContext.addShutdownable(diContext.addInit(UserIndex(indexFactory)))
    }

    private val indexedSiteIndex: IndexedSiteIndex by lazy {
        diContext.addShutdownable(diContext.addInit(IndexedSiteIndex(indexFactory)))
    }

    init {
        assert(this.seDirParser != null)
        diContext.init()
    }
}
