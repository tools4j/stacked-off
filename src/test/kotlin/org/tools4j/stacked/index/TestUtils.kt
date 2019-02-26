package org.tools4j.stacked.index

class ToListHandler<T>(val list: MutableList<T>): ItemHandler<T> {
    override fun handle(item: T) {
        list.add(item)
    }

    override fun onFinish() {
        //do nothing
    }
}

fun createPostService(): PostService {
    return PostService(
        createPostIndex(),
        createCommentIndex(),
        createUserIndex()
    )
}

fun createPostIndex(): PostIndex {
    val postIndex = PostIndex(RamIndexFactory())
    postIndex.init()
    val postXmlRowHandler = PostXmlRowHandler(postIndex.getItemHandler())
    val xmlFileParser = XmlFileParser(
        "/data/example/Posts.xml",
        XmlRowHandlerFactory(listOf(postXmlRowHandler))
    )
    xmlFileParser.parse()
    return postIndex
}

fun createCommentIndex(): CommentIndex {
    val commentIndex = CommentIndex(RamIndexFactory())
    commentIndex.init()
    val commentXmlRowHandler = CommentXmlRowHandler(commentIndex.getItemHandler())
    val xmlFileParser = XmlFileParser(
        "/data/example/Comments.xml",
        XmlRowHandlerFactory(listOf(commentXmlRowHandler))
    )
    xmlFileParser.parse()
    return commentIndex
}

fun createUserIndex(): UserIndex {
    val userIndex = UserIndex(RamIndexFactory())
    userIndex.init()
    val userXmlRowHandler = UserXmlRowHandler(userIndex.getItemHandler())
    val xmlFileParser = XmlFileParser(
        "/data/example/Users.xml",
        XmlRowHandlerFactory(listOf(userXmlRowHandler))
    )
    xmlFileParser.parse()
    return userIndex
}