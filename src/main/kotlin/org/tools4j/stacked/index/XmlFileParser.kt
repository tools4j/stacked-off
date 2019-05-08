package org.tools4j.stacked.index

import mu.KLogging
import java.io.InputStream
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLInputFactory

class XmlFileParser(val inputStream: InputStream, val xmlRowHandler: XmlRowHandler<*>) {
    private val factory = XMLInputFactory.newInstance()
    private val printCountUpdateEveryNRows = 10000;
    companion object: KLogging()

    fun parse() {
        try {
            val reader = factory.createXMLEventReader(inputStream)!!
            while (reader.hasNext()) {
                val event = reader.nextEvent()
                if (event.isStartElement()) {
                    val parentElementName = event.asStartElement().getName().getLocalPart()
                    parseElements(reader, parentElementName, xmlRowHandler)
                }
            }
        } catch (e: XmlFileParserException) {
            throw e
        } catch (e: Exception){
            throw XmlFileParserException("Error parsing file: ${e.message}", null, e);
        } finally {
            inputStream.close()
        }
    }

    private fun parseElements(reader: XMLEventReader, parentElementName: String, xmlRowHandler: XmlRowHandler<*>) {
        val startTimeMs = System.currentTimeMillis()
        var countOfElementsHandled = 0;
        while (reader.hasNext()) {
            try {
                val event = reader.nextEvent();
                if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(parentElementName)) {
                    xmlRowHandler.onFinish()
                    val endTimeMs = System.currentTimeMillis()
                    val durationMs = endTimeMs - startTimeMs
                    logger.debug{ "Total of $countOfElementsHandled $parentElementName rows read from xml. Took $durationMs ms." }
                    return;
                }
                if (event.isStartElement) {
                    val element = event.asStartElement();
                    val elementName = element.getName().getLocalPart();
                    if (elementName != "row") {
                        logger.debug{ "Exception parsing file..." }
                        throw XmlFileParserException(
                            "Found non 'row' child element within [$parentElementName] with name [$elementName]",
                            countOfElementsHandled + 1
                        )
                    }
                    xmlRowHandler.handle(element);
                    countOfElementsHandled++
                    if (countOfElementsHandled % printCountUpdateEveryNRows == 0) {
                        logger.debug{ "$countOfElementsHandled $parentElementName rows read from xml..." }
                    }
                }
            } catch (e: XmlFileParserException){
                throw e
            } catch(e: Exception){
                throw XmlFileParserException(e.message!!, countOfElementsHandled + 1, e)
            }
        }
    }
}

open class XmlFileParserException(message: String, val elementNumber: Int? = null, cause: Throwable? = null)
    : RuntimeException(message + if(elementNumber != null) " child number [$elementNumber]" else "", cause)
