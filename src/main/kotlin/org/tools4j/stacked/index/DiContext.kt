package org.tools4j.stacked.index

class DiContext {
    val initializables: MutableList<Initializable> = ArrayList();
    val shutdownables: MutableList<Shutdownable> = ArrayList();

    fun init(){
        initializables.forEach { it.init() }
    }

    fun shutdown(){
        shutdownables.forEach { it.shutdown() }
    }

    fun <T: Initializable> addInit(initializable: T): T{
        initializables.add(initializable)
        return initializable
    }

    fun <T: Shutdownable> addShutdownable(shutdownable: T): T{
        shutdownables.add(shutdownable)
        return shutdownable
    }

    fun getIndexParentDir(): String {
        return "./data"
    }
}

interface Initializable {
    fun init()
}

interface Shutdownable {
    fun shutdown()
}
