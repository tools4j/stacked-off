package org.tools4j.stacked.index

import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception

class ExceptionToString(val e: Exception) {
    override fun toString(): String {
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }
}