package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

val SITE_1 = "1"
val SITE_2 = "2"


fun getFileOnClasspath(pathOnClasspath: String): File {
    val resource = Dummy().javaClass.getResource(pathOnClasspath)
    if(resource == null){
        throw IllegalArgumentException("No resource found on classpath at: $pathOnClasspath")
    }
    return File(resource.toURI())
}

fun getFileOnClasspath(contextClass: Class<*>, pathOnClasspath: String): File {
    val resource = contextClass.getResource(pathOnClasspath)
    if(resource == null){
        throw IllegalArgumentException("No resource found on classpath at: $pathOnClasspath")
    }
    return File(resource.toURI())
}

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

fun createAndLoadPostIndex(): PostIndex {
    val postIndex = createPostIndex()
    return loadPostIndex(postIndex)
}

fun loadPostIndex(postIndex: PostIndex): PostIndex {
    val coffeeXmlFileParser = XmlFileParser(
        Dummy().javaClass.getResourceAsStream("/data/coffee/Posts.xml"),
        SITE_1,
        { PostXmlRowHandler({ postIndex.getItemHandler() }) }
    )
    coffeeXmlFileParser.parse()

    val beerXmlFileParser = XmlFileParser(
        Dummy().javaClass.getResourceAsStream("/data/beer/Posts.xml"),
        SITE_2,
        { PostXmlRowHandler({ postIndex.getItemHandler() }) }
    )
    beerXmlFileParser.parse()
    return postIndex
}

fun createPostIndex(): PostIndex {
    val postIndex = PostIndex(getTestIndexFactory())
    postIndex.init()
    postIndex.getItemHandler().onFinish()
    return postIndex
}

fun createAndLoadCommentIndex(): CommentIndex {
    val commentIndex = createCommentIndex()
    return loadCommentIndex(commentIndex)
}

fun loadCommentIndex(commentIndex: CommentIndex): CommentIndex {
    val coffeeXmlFileParser = XmlFileParser(
        Dummy().javaClass.getResourceAsStream("/data/coffee/Comments.xml"),
        SITE_1,
        { CommentXmlRowHandler({ commentIndex.getItemHandler() }) }
    )
    coffeeXmlFileParser.parse()

    val beerXmlFileParser = XmlFileParser(
        Dummy().javaClass.getResourceAsStream("/data/beer/Comments.xml"),
        SITE_2,
        { CommentXmlRowHandler({ commentIndex.getItemHandler() }) }
    )
    beerXmlFileParser.parse()

    return commentIndex
}

fun createCommentIndex(): CommentIndex {
    val commentIndex = CommentIndex(getTestIndexFactory())
    commentIndex.init()
    commentIndex.getItemHandler().onFinish()
    return commentIndex
}

fun createAndLoadUserIndex(): UserIndex {
    val userIndex = createUserIndex()
    return loadUserIndex(userIndex)
}

fun loadUserIndex(userIndex: UserIndex): UserIndex {
    val coffeeXmlFileParser = XmlFileParser(
        Dummy().javaClass.getResourceAsStream("/data/coffee/Users.xml"),
        SITE_1,
        { UserXmlRowHandler({ userIndex.getItemHandler() }) }
    )
    coffeeXmlFileParser.parse()

    val beerXmlFileParser = XmlFileParser(
        Dummy().javaClass.getResourceAsStream("/data/beer/Users.xml"),
        SITE_2,
        { UserXmlRowHandler({ userIndex.getItemHandler() }) }
    )
    beerXmlFileParser.parse()

    return userIndex
}

fun createUserIndex(): UserIndex {
    val userIndex = UserIndex(getTestIndexFactory())
    userIndex.init()
    userIndex.getItemHandler().onFinish()
    return userIndex
}

//TODO, work how to get resourceAsStream easier
class Dummy{}


fun createAndLoadIndexedSiteIndex(): IndexedSiteIndex{
    val indexedSiteIndex = createIndexedSiteIndex()
    return loadIndexedSiteIndex(indexedSiteIndex)
}

fun loadIndexedSiteIndex(indexedSiteIndex: IndexedSiteIndex): IndexedSiteIndex {
    val xmlRowParser = SeSiteXmlFileParser(Dummy().javaClass.getResourceAsStream("/data/Sites.xml"));
    val sites = xmlRowParser.parse()

    val beerIndexedSite = IndexedSiteImpl(
        "1",
        "2019-02-25T10:00:00",
        true,
        null,
        sites.first { it.tinyName == "beerme" })

    val sw = StringWriter()
    IllegalArgumentException("Boom!").printStackTrace(PrintWriter(sw))
    val exceptionAsString = sw.toString()

    val coffeeIndexedSite = IndexedSiteImpl(
        "2",
        "2019-02-01T05:00:00",
        false,
        exceptionAsString,
        sites.first { it.tinyName == "coffeeme" })

    indexedSiteIndex.addItems(listOf(beerIndexedSite, coffeeIndexedSite))
    return indexedSiteIndex
}

fun createIndexedSiteIndex(): IndexedSiteIndex {
    val indexedSiteIndex = IndexedSiteIndex(getTestIndexFactory())
    indexedSiteIndex.init()
    indexedSiteIndex.getItemHandler().onFinish()
    return indexedSiteIndex
}

private fun getTestIndexFactory() = LightweightIndexFactory()

fun createIndexes(): Indexes {
    return Indexes(
        createIndexedSiteIndex(),
        createPostIndex(),
        createCommentIndex(),
        createUserIndex())
}

fun assertHasIndexedSite1(indexedSites: List<IndexedSite>){
    assertIsIndexedSite1(indexedSites.first { it.indexedSiteId == "1" })
}

fun assertHasIndexedSite2(indexedSites: List<IndexedSite>){
    assertIsIndexedSite2(indexedSites.first { it.indexedSiteId == "2" })
}

fun assertIsIndexedSite1(indexedSite: IndexedSite){
    assertThat(indexedSite.indexedSiteId).isEqualTo("1")
    assertThat(indexedSite.dateTimeIndexed).isEqualTo("2019-02-25T10:00:00")
    assertThat(indexedSite.success).isEqualTo(true)
    assertThat(indexedSite.errorMessage).isNull()
    assertIsSeSite2(indexedSite.seSite)
}

fun assertIsIndexedSite2(indexedSite: IndexedSite){
    assertThat(indexedSite.indexedSiteId).isEqualTo("2")
    assertThat(indexedSite.dateTimeIndexed).isEqualTo("2019-02-01T05:00:00")
    assertThat(indexedSite.success).isEqualTo(false)
    assertThat(indexedSite.errorMessage).startsWith("java.lang.IllegalArgumentException: Boom!")
    assertIsSeSite3(indexedSite.seSite)
}