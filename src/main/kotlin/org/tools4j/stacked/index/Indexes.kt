package org.tools4j.stacked.index

import mu.KLogging

class Indexes(val indexedSiteIndex: IndexedSiteIndex,
              val postIndex: PostIndex,
              val commentIndex: CommentIndex,
              val userIndex: UserIndex){

    companion object: KLogging()

    fun purgeSite(indexedSiteId: String){
        logger.debug{ "Purging site with id: $indexedSiteId" }
        indexedSiteIndex.purgeSite(indexedSiteId)
        postIndex.purgeSite(indexedSiteId)
        commentIndex.purgeSite(indexedSiteId)
        userIndex.purgeSite(indexedSiteId)
    }

    fun purgeSites(sites: List<IndexedSite>) {
        for (matchingExistingIndexedSite in sites) {
            purgeSite(matchingExistingIndexedSite.indexedSiteId)
        }
    }
}