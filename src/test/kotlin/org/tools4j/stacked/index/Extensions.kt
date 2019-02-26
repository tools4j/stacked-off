package org.tools4j.stacked.index

import javax.xml.namespace.QName
import javax.xml.stream.events.StartElement

fun StartElement.getAttribute(name: String): String? {
    return this.getAttributeByName(QName.valueOf(name))?.value
}