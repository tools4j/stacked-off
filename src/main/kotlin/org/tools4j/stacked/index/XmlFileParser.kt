package org.tools4j.stacked.index

import java.io.InputStream
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLInputFactory


class XmlFileParser(val fileInputStream: InputStream, val indexedSiteId: String, val xmlRowHandlerProvider: () -> XmlRowHandler<*>) {
    private val factory = XMLInputFactory.newInstance()
    private val printCountUpdateEveryNRows = 10;

    fun parse(){
        val reader = factory.createXMLEventReader(fileInputStream)!!
        while (reader.hasNext()) {
            val event = reader.nextEvent()
            if(event.isStartElement()){
                val parentElementName = event.asStartElement().getName().getLocalPart()
                val xmlRowHandler = xmlRowHandlerProvider()
                parseElements(reader, parentElementName, xmlRowHandler)
            }
        }
    }

    private fun parseElements(reader: XMLEventReader, parentElementName: String, xmlRowHandler: XmlRowHandler<*>) {
        val startTimeMs = System.currentTimeMillis()
        var countOfElementsHandled = 0;
        while (reader.hasNext()) {
            val event = reader.nextEvent();
            if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(parentElementName)) {
                xmlRowHandler.onFinish()
                val endTimeMs = System.currentTimeMillis()
                val durationMs = endTimeMs - startTimeMs
                println("Total of $countOfElementsHandled $parentElementName rows read from xml. Took $durationMs ms.")
                return;
            }
            if (event.isStartElement()) {
                val element = event.asStartElement();
                val elementName = element.getName().getLocalPart();
                if(elementName != "row"){
                    throw IllegalStateException("Found non 'row' child element: " + element)
                }
                xmlRowHandler.handle(element, indexedSiteId);
                countOfElementsHandled++
                if(countOfElementsHandled % printCountUpdateEveryNRows == 0){
                    println("$countOfElementsHandled $parentElementName rows read from xml...")
                }
            }
        }
    }
}