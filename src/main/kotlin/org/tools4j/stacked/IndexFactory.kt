package org.tools4j.stacked;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory

interface IndexFactory {
    fun createIndex(name: String): Directory
}

class RamIndexFactory: IndexFactory {
    override fun createIndex(name: String): Directory {
        return RAMDirectory()
    }
}
