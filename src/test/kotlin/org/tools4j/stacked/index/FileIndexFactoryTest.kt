package org.tools4j.stacked.index

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

internal class FileIndexFactoryTest {
    val indexDir = File("./stagingIndexes")

    @BeforeEach
    fun beforeEach(){
        if(indexDir.exists()) indexDir.deleteRecursively()
    }

    @Test
    fun testCreateIndex() {
        val postIndex = StagingPostIndex(FileIndexFactory("./stagingIndexes"))
        try {
            postIndex.init()
            coffeeSiteIndexUtils.loadPostIndex(postIndex);
            assertThat(postIndex.getById("1")).isNotNull
        } finally {
            postIndex.shutdown()
        }
    }

    @Test
    fun testCreateThenReloadIndex() {
        val postIndex = StagingPostIndex(FileIndexFactory("./stagingIndexes"))
        try {
            postIndex.init()
            coffeeSiteIndexUtils.loadPostIndex(postIndex);
            assertThat(postIndex.getById("1")).isNotNull
            postIndex.shutdown()
        } finally {
            postIndex.shutdown()
        }

        val postIndexReloaded = StagingPostIndex(FileIndexFactory("./stagingIndexes"))
        postIndexReloaded.init()
        try {
            assertThat(postIndex.getById("1")).isNotNull
        } finally {
            postIndex.shutdown()
        }
    }
}