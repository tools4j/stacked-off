package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

val beerSiteIndexUtils = SiteIndexUtils("/data/beer", "1")
val coffeeSiteIndexUtils = SiteIndexUtils("/data/coffee", "2")

fun getFileOnClasspath(pathOnClasspath: String): File {
    val resource = Dummy().javaClass.getResource(pathOnClasspath)
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

fun createPostIndex(): StagingPostIndex {
    val postIndex = StagingPostIndex(getTestIndexFactory())
    postIndex.init()
    postIndex.getItemHandler().onFinish()
    return postIndex
}

fun createCommentIndex(): StagingCommentIndex {
    val commentIndex = StagingCommentIndex(getTestIndexFactory())
    commentIndex.init()
    commentIndex.getItemHandler().onFinish()
    return commentIndex
}

fun createQuestionIndex(indexedSiteIndex: IndexedSiteIndex): QuestionIndex {
    val questionIndex = QuestionIndex(getTestIndexFactory(), indexedSiteIndex)
    questionIndex.init()
    return questionIndex
}


fun createAndLoadQuestionIndex(): QuestionIndex {
    val indexedSiteIndex = createAndLoadIndexedSiteIndex()
    val questionIndex = createQuestionIndex(indexedSiteIndex)
    QuestionIndexer(beerSiteIndexUtils.createAndLoadStagingIndexes(), beerSiteIndexUtils.siteId, questionIndex).index()
    QuestionIndexer(coffeeSiteIndexUtils.createAndLoadStagingIndexes(), coffeeSiteIndexUtils.siteId, questionIndex).index()
    return questionIndex
}


fun createUserIndex(): StagingUserIndex {
    val userIndex = StagingUserIndex(getTestIndexFactory())
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
    indexedSiteIndex.onNewDataAddedToIndex()
    return indexedSiteIndex
}

fun createIndexedSiteIndex(): IndexedSiteIndex {
    val indexedSiteIndex = IndexedSiteIndex(getTestIndexFactory())
    indexedSiteIndex.init()
    indexedSiteIndex.getItemHandler().onFinish()
    return indexedSiteIndex
}

fun getTestIndexFactory() = LightweightIndexFactory()

fun createStagingIndexes(): StagingIndexes {
    return StagingIndexes(
        createPostIndex(),
        createCommentIndex(),
        createUserIndex())
}

fun createIndexes(): Indexes {
    val indexedSiteIndex = createIndexedSiteIndex()
    return Indexes(
        indexedSiteIndex,
        createQuestionIndex(indexedSiteIndex),
        createStagingIndexes())
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

fun createXmlRowHandlers(stagingIndexes: StagingIndexes): Map<String, XmlRowHandler<out Any>> {
    return mapOf(
        "Posts.xml" to PostXmlRowHandler(stagingIndexes.stagingPostIndex.getItemHandler()),
        "Users.xml" to UserXmlRowHandler(stagingIndexes.stagingUserIndex.getItemHandler()),
        "Comments.xml" to CommentXmlRowHandler(stagingIndexes.stagingCommentIndex.getItemHandler()))
}