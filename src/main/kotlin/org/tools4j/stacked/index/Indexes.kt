package org.tools4j.stacked.index

class Indexes(val indexedSiteIndex: IndexedSiteIndex,
              val postIndex: PostIndex,
              val commentIndex: CommentIndex,
              val userIndex: UserIndex){

    fun purgeSite(indexedSiteId: String){
        println("Purging site with id: $indexedSiteId")
        indexedSiteIndex.purgeSite(indexedSiteId)
        postIndex.purgeSite(indexedSiteId)
        commentIndex.purgeSite(indexedSiteId)
        userIndex.purgeSite(indexedSiteId)
    }

    fun purgeSites(matchingExistingIndexedSites: List<IndexedSite>) {
        for (matchingExistingIndexedSite in matchingExistingIndexedSites) {
            purgeSite(matchingExistingIndexedSite.indexedSiteId)
        }
    }
}