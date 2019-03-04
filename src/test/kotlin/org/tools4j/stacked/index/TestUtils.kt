package org.tools4j.stacked.index

val SITE_1 = "1"
val SITE_2 = "2"

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
        createAndLoadPostIndex(),
        createAndLoadCommentIndex(),
        createAndLoadUserIndex()
    )
}

fun createIndexRepo(): IndexRepo {
    return IndexRepo(
        createSiteIndex(),
        createPostIndex(),
        createCommentIndex(),
        createUserIndex()
    )
}

fun createRowHandlerRepo(indexRepo: IndexRepo): XmlRowHandlerRepo {
    return XmlRowHandlerRepo(
        linkedMapOf(
            "posts" to {PostXmlRowHandler({indexRepo.postIndex.getItemHandler()})},
            "comments" to {CommentXmlRowHandler({indexRepo.commentIndex.getItemHandler()})},
            "users" to {UserXmlRowHandler({indexRepo.userIndex.getItemHandler()})}
        )
    )
}

fun createAndLoadPostIndex(): PostIndex {
    val postIndex = createPostIndex()
    val coffeeXmlFileParser = XmlFileParser(
        "/data/coffee/Posts.xml",
        SITE_1,
        {PostXmlRowHandler({postIndex.getItemHandler()})}
    )
    coffeeXmlFileParser.parse()

    val beerXmlFileParser = XmlFileParser(
        "/data/beer/Posts.xml",
        SITE_2,
        {PostXmlRowHandler({postIndex.getItemHandler()})}
    )
    beerXmlFileParser.parse()
    return postIndex
}

private fun createPostIndex(): PostIndex {
    val postIndex = PostIndex(RamIndexFactory())
    postIndex.init()
    return postIndex
}

fun createAndLoadCommentIndex(): CommentIndex {
    val commentIndex = createCommentIndex()

    val coffeeXmlFileParser = XmlFileParser(
        "/data/coffee/Comments.xml",
        SITE_1,
        {CommentXmlRowHandler({commentIndex.getItemHandler()})}
    )
    coffeeXmlFileParser.parse()

    val beerXmlFileParser = XmlFileParser(
        "/data/beer/Comments.xml",
        SITE_2,
        {CommentXmlRowHandler({commentIndex.getItemHandler()})}
    )
    beerXmlFileParser.parse()

    return commentIndex
}

private fun createCommentIndex(): CommentIndex {
    val commentIndex = CommentIndex(RamIndexFactory())
    commentIndex.init()
    return commentIndex
}

fun createAndLoadUserIndex(): UserIndex {
    val userIndex = createUserIndex()

    val coffeeXmlFileParser = XmlFileParser(
        "/data/coffee/Users.xml",
        SITE_1,
        {UserXmlRowHandler({userIndex.getItemHandler()})}
    )
    coffeeXmlFileParser.parse()

    val beerXmlFileParser = XmlFileParser(
        "/data/beer/Users.xml",
        SITE_2,
        {UserXmlRowHandler({userIndex.getItemHandler()})}
    )
    beerXmlFileParser.parse()

    return userIndex
}

private fun createUserIndex(): UserIndex {
    val userIndex = UserIndex(RamIndexFactory())
    userIndex.init()
    return userIndex
}

fun createAndLoadSiteIndex(): SiteIndex {
    val index = createSiteIndex()
    val xmlFileParser = SiteXmlFileParser("/data/Sites.xml", index)
    xmlFileParser.parse()
    return index
}

private fun createSiteIndex(): SiteIndex {
    val index = SiteIndex(RamIndexFactory())
    index.init()
    return index
}