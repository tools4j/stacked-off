package org.tools4j.stacked.index

import io.ktor.util.error

class SeDirParserListener(private val indexes: Indexes): ParseSiteListener {

    override fun onStartParseSite(indexedSite: IndexedSite) {
        indexes.stagingIndexes.purge()
        indexes.indexedSiteIndex.addItem(indexedSite)
        indexes.indexedSiteIndex.onIndexDataChange()
    }

    override fun onFinishParseSite(
        indexedSite: IndexedSite,
        jobStatus: JobStatus
    ) {
        indexes.indexedSiteIndex.purgeSite(indexedSite.indexedSiteId)
        indexes.indexedSiteIndex.addItem(indexedSite)

        if(indexedSite.status != Status.ERROR){
            indexes.indexedSiteIndex.purgeSite(indexedSite.indexedSiteId)
            indexes.indexedSiteIndex.addItem(indexedSite.withStatus(Status.LINKING_STAGING_INDICES))
            indexes.stagingIndexes.onIndexDataChange()
            jobStatus.addOperation("Linking staging indexes ${indexedSite.seSite.urlDomain}")
            try {
                QuestionIndexer(
                    indexes.stagingIndexes,
                    indexedSite.indexedSiteId,
                    indexes.questionIndex,
                    jobStatus
                ).index()
                val olderVersionsOfThisSite = indexes.indexedSiteIndex.getMatching(indexedSite.seSite)
                    .map { it.indexedSiteId }
                    .filter { it != indexedSite.indexedSiteId }
                indexes.indexedSiteIndex.purgeSite(indexedSite.indexedSiteId)
                indexes.indexedSiteIndex.purgeSites(olderVersionsOfThisSite)
                indexes.questionIndex.purgeSites(olderVersionsOfThisSite)
                indexes.indexedSiteIndex.addItem(indexedSite.withStatus(Status.LOADED))
                indexes.stagingIndexes.purge()

            } catch (e: Exception){
                logger.error(e)
                indexes.questionIndex.purgeSite(indexedSite.indexedSiteId)
                indexes.indexedSiteIndex.purgeSite(indexedSite.indexedSiteId)
                val errorMessage = ExceptionToString(e).toString()
                indexes.indexedSiteIndex.addItem(indexedSite.withStatus(Status.ERROR, errorMessage))
                jobStatus.addOperation("Error parsing site ${indexedSite.seSite.urlDomain}:\n${errorMessage}")
            }
        } else {
            jobStatus.addOperation("Error parsing site ${indexedSite.seSite.urlDomain}:\n${indexedSite.errorMessage}")
        }
    }
}