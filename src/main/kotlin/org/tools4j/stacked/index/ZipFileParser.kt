package org.tools4j.stacked.index


class ZipFileParser(val file: String, val xmlFileParser: XmlFileParser) {
    /*
    private val factory = XMLInputFactory.newInstance()
    private val printCountUpdateEveryNRows = 10;

    @Throws(IOException::class, XMLStreamException::class)
    fun parse() {
        this.javaClass.getResourceAsStream(file).use { stream ->
//            ZipInputStream(stream).use { zip ->
                val reader = factory.createXMLEventReader(stream)!!
                while (reader.hasNext()) {
                    val event = reader.nextEvent()
                    if(event.isStartElement()){
                        val parentElementName = event.asStartElement().getName().getLocalPart()
                        val xmlRowHandler = xmlRowHandlerFactory.getHandlerForElementName(parentElementName)
                        parseElements(reader, parentElementName, xmlRowHandler)
                    }
                }
//            }
        }
    }

    private fun parseElements(reader: XMLEventReader, parentElementName: String, xmlRowHandler: XmlRowHandler<*>) {
        var countOfElementsHandled = 0;
        while (reader.hasNext()) {
            val event = reader.nextEvent();
            if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(parentElementName)) {
                xmlRowHandler.onFinish()
                println("Total of $countOfElementsHandled $parentElementName rows read.")
                return;
            }
            if (event.isStartElement()) {
                val element = event.asStartElement();
                val elementName = element.getName().getLocalPart();
                if(elementName != "row"){
                    throw IllegalStateException("Found non 'row' child element: " + element)
                }
                xmlRowHandler.handle(element);
                countOfElementsHandled++
                if(countOfElementsHandled % printCountUpdateEveryNRows == 0){
                    println("$countOfElementsHandled $parentElementName rows read...")
                }
            }
        }
    }
    */
}