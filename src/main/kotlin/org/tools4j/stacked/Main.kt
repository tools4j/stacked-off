package org.tools4j.stacked

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class Main {

    companion object {
        internal val kotlinXmlMapper = XmlMapper(JacksonXmlModule().apply {
            setDefaultUseWrapper(false)
        }).registerKotlinModule()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


        internal inline fun <reified T : Any> parseAs(path: String): T {
            val resource = this.javaClass.classLoader.getResource(path)
            return kotlinXmlMapper.readValue(resource)
        }

        @JvmStatic
        fun main(args: Array<String>) {
        }
    }
}