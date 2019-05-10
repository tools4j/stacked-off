package org.tools4j.stacked.index

class IndexStats(indexes: Indexes) {
    val indexSizes = HashMap<String, Int>()

    init {
        indexSizes["questionIndex"] = indexes.questionIndex.size()
        indexSizes["indexedSiteIndex"] = indexes.indexedSiteIndex.size()
        indexSizes["stagingPostIndex"] = indexes.stagingIndexes.stagingPostIndex.size()
        indexSizes["stagingCommentIndex"] = indexes.stagingIndexes.stagingCommentIndex.size()
        indexSizes["stagingUserIndex"] = indexes.stagingIndexes.stagingUserIndex.size()
    }
}
