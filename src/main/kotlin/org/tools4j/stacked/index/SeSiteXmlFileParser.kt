package org.tools4j.stacked.index

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.StartElement


class SeSiteXmlFileParser(val inputStream: InputStream) {
    private val factory = XMLInputFactory.newInstance()
    private val printCountUpdateEveryNRows = 10;

    @Throws(IOException::class, XMLStreamException::class)
    fun parse(): Set<SeSite> {
        val reader = factory.createXMLEventReader(inputStream)!!
        while (reader.hasNext()) {
            val event = reader.nextEvent()
            if(event.isStartElement && event.asStartElement().name.localPart == "sites"){
                return parseElements(reader)
            }
        }
        return emptySet()
    }

    private fun parseElements(reader: XMLEventReader): Set<SeSite> {
        val sites = LinkedHashSet<SeSite>()
        val startTimeMs = System.currentTimeMillis()
        var countOfElementsHandled = 0;
        while (reader.hasNext()) {
            val event = reader.nextEvent();
            if (event.isEndElement() && event.asEndElement().name.localPart == "sites") {
                val endTimeMs = System.currentTimeMillis()
                val durationMs = endTimeMs - startTimeMs
                println("Total of $countOfElementsHandled site rows read from xml. Took $durationMs ms.")
                return sites;
            }
            if (event.isStartElement()) {
                val element = event.asStartElement();
                val elementName = element.getName().getLocalPart();
                if(elementName != "row"){
                    throw IllegalStateException("Found non 'row' child element: " + element)
                }
                sites.add(handle(element));
                countOfElementsHandled++
                if(countOfElementsHandled % printCountUpdateEveryNRows == 0){
                    println("$countOfElementsHandled sites rows read from xml...")
                }
            }
        }
        return sites
    }

    fun handle(element: StartElement): SeSite {
        return SeSiteImpl(
            element.getAttributeByName(QName.valueOf("Id"))!!.value,
            element.getAttributeByName(QName.valueOf("TinyName"))!!.value,
            element.getAttributeByName(QName.valueOf("Name"))!!.value,
            element.getAttributeByName(QName.valueOf("LongName"))?.value,
            element.getAttributeByName(QName.valueOf("Url")).value,
            element.getAttributeByName(QName.valueOf("ImageUrl"))?.value,
            element.getAttributeByName(QName.valueOf("IconUrl"))?.value,
            element.getAttributeByName(QName.valueOf("DatabaseName"))?.value,
            element.getAttributeByName(QName.valueOf("Tagline"))?.value,
            element.getAttributeByName(QName.valueOf("TagCss"))?.value,
            element.getAttributeByName(QName.valueOf("TotalQuestions"))?.value,
            element.getAttributeByName(QName.valueOf("TotalAnswers"))?.value,
            element.getAttributeByName(QName.valueOf("TotalUsers"))?.value,
            element.getAttributeByName(QName.valueOf("TotalComments"))?.value,
            element.getAttributeByName(QName.valueOf("TotalTags"))?.value,
            element.getAttributeByName(QName.valueOf("LastPost"))?.value,
            element.getAttributeByName(QName.valueOf("ODataEndpoint"))?.value,
            element.getAttributeByName(QName.valueOf("BadgeIconUrl"))?.value
        )
    }
}