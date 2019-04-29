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
        val postIndex = PostIndex(FileIndexFactory("./stagingIndexes"))
        try {
            postIndex.init()
            loadPostIndex(postIndex);
            assertThat(postIndex.search("coffee")).isNotEmpty
        } finally {
            postIndex.shutdown()
        }
    }

    @Test
    fun testCreateThenReloadIndex() {
        val postIndex = PostIndex(FileIndexFactory("./stagingIndexes"))
        try {
            postIndex.init()
            loadPostIndex(postIndex);
            assertThat(postIndex.search("coffee")).isNotEmpty
            postIndex.shutdown()
        } finally {
            postIndex.shutdown()
        }

        val postIndexReloaded = PostIndex(FileIndexFactory("./stagingIndexes"))
        postIndexReloaded.init()
        try {
            assertThat(postIndexReloaded.search("coffee")).isNotEmpty
        } finally {
            postIndex.shutdown()
        }
    }
}