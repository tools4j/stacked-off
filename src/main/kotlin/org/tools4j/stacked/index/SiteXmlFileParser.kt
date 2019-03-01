package org.tools4j.stacked.index

import java.io.IOException
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.events.StartElement


class SiteXmlFileParser(val file: String, siteIndex: SiteIndex) {
    private val factory = XMLInputFactory.newInstance()
    private val printCountUpdateEveryNRows = 10;
    private val siteIndexHandler: ItemHandler<Site> by lazy { siteIndex.getItemHandler() }

    @Throws(IOException::class, XMLStreamException::class)
    fun parse() {
        this.javaClass.getResourceAsStream(file).use { stream ->
            val reader = factory.createXMLEventReader(stream)!!
            while (reader.hasNext()) {
                val event = reader.nextEvent()
                if(event.isStartElement()){
                    val parentElementName = event.asStartElement().getName().getLocalPart()
                    parseElements(reader)
                }
            }
        }
    }

    private fun parseElements(reader: XMLEventReader) {
        val startTimeMs = System.currentTimeMillis()
        var countOfElementsHandled = 0;
        while (reader.hasNext()) {
            val event = reader.nextEvent();
            if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("sites")) {
                siteIndexHandler.onFinish()
                val endTimeMs = System.currentTimeMillis()
                val durationMs = endTimeMs - startTimeMs
                println("Total of $countOfElementsHandled site rows read from xml. Took $durationMs ms.")
                return;
            }
            if (event.isStartElement()) {
                val element = event.asStartElement();
                val elementName = element.getName().getLocalPart();
                if(elementName != "row"){
                    throw IllegalStateException("Found non 'row' child element: " + element)
                }
                handle(element);
                countOfElementsHandled++
                if(countOfElementsHandled % printCountUpdateEveryNRows == 0){
                    println("$countOfElementsHandled sites rows read from xml...")
                }
            }
        }
    }

    fun handle(element: StartElement) {
        val site = SiteImpl(
            element.getAttributeByName(QName.valueOf("Id"))!!.value,
            element.getAttributeByName(QName.valueOf("TinyName"))?.value,
            element.getAttributeByName(QName.valueOf("Name"))?.value,
            element.getAttributeByName(QName.valueOf("LongName"))?.value,
            element.getAttributeByName(QName.valueOf("Url"))?.value,
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
        siteIndexHandler.handle(site)
    }
}