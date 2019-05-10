package org.tools4j.stacked.index

class SeDirParserListener(private val indexes: Indexes): ParseSiteListener {

    override fun onStartParseSite(seSite: SeSite) {
        indexes.stagingIndexes.purge()
    }

    override fun onFinishParseSite(
        indexedSite: IndexedSite,
        jobStatus: JobStatus
    ) {
        if(indexedSite.success){
            indexes.stagingIndexes.onNewDataAddedToIndexes()
            jobStatus.addOperation("Finished parsing site ${indexedSite.seSite.urlDomain}")
            try {
                QuestionIndexer(
                    indexes.stagingIndexes,
                    indexedSite.indexedSiteId,
                    indexes.questionIndex,
                    jobStatus
                ).index()
                val matchingExistingIndexedSiteIds = indexes.indexedSiteIndex.getMatching(indexedSite.seSite).map { it.indexedSiteId }
                indexes.indexedSiteIndex.purgeSites(matchingExistingIndexedSiteIds)
                indexes.questionIndex.purgeSites(matchingExistingIndexedSiteIds)
                indexes.indexedSiteIndex.addItem(indexedSite)
                indexes.stagingIndexes.purge()

            } catch (e: Exception){
                indexes.questionIndex.purgeSite(indexedSite.indexedSiteId)
                indexes.indexedSiteIndex.addItem(IndexingSiteImpl(indexedSite).finished(false, ExceptionToString(e).toString()))
            }
        } else {
            jobStatus.addOperation("Error parsing site ${indexedSite.seSite.urlDomain}:\n${indexedSite.errorMessage}")
            indexes.indexedSiteIndex.addItem(indexedSite)
        }
    }
}