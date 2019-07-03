package org.tools4j.stacked.index

import java.io.PrintWriter
import java.io.StringWriter

class ExceptionToString(val e: Throwable) {
    override fun toString(): String {
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }
}