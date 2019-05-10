package org.tools4j.stacked.index

import mu.KLogging

class StagingIndexes(val stagingPostIndex: StagingPostIndex,
                     val stagingCommentIndex: StagingCommentIndex,
                     val stagingUserIndex: StagingUserIndex){

    companion object: KLogging()

    fun purge(){
        logger.debug{ "Purging staging indexes" }
        stagingPostIndex.purge()
        stagingCommentIndex.purge()
        stagingUserIndex.purge()
    }

    fun onNewDataAddedToIndexes() {
        stagingPostIndex.onNewDataAddedToIndex()
        stagingCommentIndex.onNewDataAddedToIndex()
        stagingUserIndex.onNewDataAddedToIndex()
    }
}