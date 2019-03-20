package org.tools4j.stacked.index;

import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.store.RAMDirectory
import java.nio.file.Paths


interface IndexFactory {
    fun createIndex(name: String): Directory
}

class LightweightIndexFactory: IndexFactory {
    override fun createIndex(name: String): Directory {
        return RAMDirectory()
    }
}

class FileIndexFactory(private val indexParentDir: String): IndexFactory {
    override fun createIndex(name: String): Directory {
        return FSDirectory.open(Paths.get("${indexParentDir}/${name}.lucene"))
    }
}
