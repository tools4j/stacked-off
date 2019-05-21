package org.tools4j.stacked.index

import mu.KLogging
import java.util.*

class DiContext {
    companion object: KLogging()
    val INDEX_DIR_PROP = "index.dir"
    val initializables: MutableList<Initializable> = ArrayList();
    val shutdownables: MutableList<Shutdownable> = ArrayList();
    var initialized = false

    val properties: Properties by lazy {
        getOrCreateStackedOffProperties()
    }

    fun init(){
        synchronized(this) {
            initializables.forEach {
                logger.info { "Initializing: $it" }
                it.init()
            }
            initialized = true
        }
    }

    fun shutdown(){
        shutdownables.forEach { it.shutdown() }
    }

    fun <T: Initializable> addInit(initializable: T): T{
        synchronized(this) {
            initializables.add(initializable)
            if(initialized){
                logger.info { "Lazy initialization of $initializable" }
                initializable.init()
            }
            return initializable
        }
    }

    fun <T: Shutdownable> addShutdownable(shutdownable: T): T{
        shutdownables.add(shutdownable)
        return shutdownable
    }

//    fun getIndexParentDir(): String? {
//        return "./data"
//    }

    fun getIndexParentDir(): String? {
        return properties.getProperty(INDEX_DIR_PROP)
    }

    fun setIndexParentDir(indexParentDir: String) {
        properties.setProperty(INDEX_DIR_PROP, indexParentDir)
        saveStackedOffUserProperties(properties)
    }
}

interface Initializable {
    fun init()
}

interface Shutdownable {
    fun shutdown()
}
