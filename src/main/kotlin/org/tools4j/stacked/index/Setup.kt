package org.tools4j.stacked.index

class Setup {
    open val diContext: DiContext by lazy {
        DiContext()
    }

    open val seSeFileInZipParserProvider: SeFileInZipParserProvider by lazy {
        SeFileInZipParserProviderImpl(xmlRowHandlersByFileName);
    }

    open val seZipFileParser: SeZipFileParser by lazy {
        SeZipFileParser(seSeFileInZipParserProvider)
    }

    open val seDirParser: SeDirParser by lazy {
        SeDirParser(seZipFileParser, indexes)
    }

    open val xmlRowHandlersByFileName: Map<String, () -> XmlRowHandler<*>> by lazy {
        mapOf(
            "Posts.xml" to { PostXmlRowHandler { postIndex.getItemHandler() } },
            "Users.xml" to { UserXmlRowHandler { userIndex.getItemHandler() } },
            "Comments.xml" to { CommentXmlRowHandler { commentIndex.getItemHandler() } })
    }

    open val indexFactory: IndexFactory by lazy {
        FileIndexFactory(diContext.getIndexParentDir())
    }

    open val indexes: Indexes by lazy {
        Indexes(indexedSiteIndex, postIndex, commentIndex, userIndex)
    }

    open val postIndex: PostIndex by lazy {
        diContext.addInit(PostIndex(indexFactory))
    }

    open val commentIndex: CommentIndex by lazy {
        diContext.addInit(CommentIndex(indexFactory))
    }

    open val userIndex: UserIndex by lazy {
        diContext.addInit(UserIndex(indexFactory))
    }

    open val indexedSiteIndex: IndexedSiteIndex by lazy {
        diContext.addInit(IndexedSiteIndex(indexFactory))
    }
}
