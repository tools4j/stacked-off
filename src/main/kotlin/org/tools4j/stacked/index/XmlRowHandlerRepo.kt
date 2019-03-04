package org.tools4j.stacked.index

import java.lang.IllegalStateException
import javax.xml.stream.events.StartElement

class XmlRowHandlerRepo(val rowHandlerProvidersByName: Map<String, () -> XmlRowHandler<*>>) {
    fun getHandlerForElementName(parentElementName: String): XmlRowHandler<*> {
        if(!rowHandlerProvidersByName.containsKey(parentElementName)){
            throw IllegalStateException("No handler found for parentElementName [$parentElementName], supported " +
                    "handlers registered: ${rowHandlerProvidersByName.keys}")
        }
        return rowHandlerProvidersByName.getValue(parentElementName)()
    }
}

abstract class XmlRowHandler<T>(val delegateProvider: () -> ItemHandler<T>) {
    abstract fun getParentElementName(): String
    abstract fun handle(element: StartElement, indexedSiteId: String)
    internal val delegate: ItemHandler<T> by lazy {delegateProvider()}

    fun onFinish() {
        delegate.onFinish()
    }
}
