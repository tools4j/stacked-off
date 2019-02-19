package org.tools4j.stacked

import javax.xml.stream.events.StartElement

abstract class XmlRowHandler<T>(val delegate: ItemHandler<T>): ItemHandler<StartElement> {

    abstract fun getParentElementName(): String

    override fun onFinish() {
        delegate.onFinish()
    }
}
