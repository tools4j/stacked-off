package org.tools4j.stacked.web

import org.tools4j.stacked.index.Instance

class PerfTest(var instance: Instance) {
    fun run(): Long {
        //run test
        val startMs = System.currentTimeMillis()
        val iterations = 20
        for (i in 0 ..iterations){
            instance.questionIndex.searchForQuestionSummaries("java python", 0, 10, false)
            instance.questionIndex.searchForQuestionSummaries("windows control panel", 0, 10, false)
            instance.questionIndex.searchForQuestionSummaries("mac usb device", 0, 10, false)
            instance.questionIndex.searchForQuestionSummaries("perf testing", 0, 10, false)
            instance.questionIndex.searchForQuestionSummaries("intellij jetbrains", 0, 10, false)
        }
        val durationMs = System.currentTimeMillis() - startMs
        println("Total time: " + durationMs.toString() + "ms")
        println("Avg time per query: " + (durationMs / 5 / iterations) + "ms")
        return durationMs
    }
}
