package org.tools4j.stacked

import java.lang.IllegalStateException

class XmlRowHandlerFactory(val rowHandlers: List<XmlRowHandler<*>>) {
    val rowHandlersByName = rowHandlers.map{it.getParentElementName() to it}.toMap()

    fun getHandlerForElementName(parentElementName: String): XmlRowHandler<*> {
        if(!rowHandlersByName.containsKey(parentElementName)){
            throw IllegalStateException("No handler found for parentElementName [$parentElementName], supported " +
                    "handlers registered: ${rowHandlersByName.keys}")
        }
        return rowHandlersByName.getValue(parentElementName)
    }
}