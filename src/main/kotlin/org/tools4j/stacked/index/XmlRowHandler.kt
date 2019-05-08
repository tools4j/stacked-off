package org.tools4j.stacked.index

import javax.xml.stream.events.StartElement

abstract class XmlRowHandler<T>(val delegate: ItemHandler<T>) {
    abstract fun handle(element: StartElement)

    fun onFinish() {
        delegate.onFinish()
    }
}
