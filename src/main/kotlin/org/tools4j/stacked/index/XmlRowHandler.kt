package org.tools4j.stacked.index

import javax.xml.stream.events.StartElement

abstract class XmlRowHandler<T>(val delegateProvider: () -> ItemHandler<T>) {
    abstract fun handle(element: StartElement, indexedSiteId: String)
    internal val delegate: ItemHandler<T> by lazy {delegateProvider()}

    fun onFinish() {
        delegate.onFinish()
    }
}
