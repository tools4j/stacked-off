package org.tools4j.stacked.web

class PerfTest(var webDi: WebDi) {
    fun run(): Long {
        //run test
        val startMs = System.currentTimeMillis()
        val iterations = 20
        for (i in 0 ..iterations){
            webDi.questionIndex.searchForQuestionSummaries("java python", 0, 10, false)
            webDi.questionIndex.searchForQuestionSummaries("windows control panel", 0, 10, false)
            webDi.questionIndex.searchForQuestionSummaries("mac usb device", 0, 10, false)
            webDi.questionIndex.searchForQuestionSummaries("perf testing", 0, 10, false)
            webDi.questionIndex.searchForQuestionSummaries("intellij jetbrains", 0, 10, false)
        }
        val durationMs = System.currentTimeMillis() - startMs
        println("Total time: " + durationMs.toString() + "ms")
        println("Avg time per query: " + (durationMs / 5 / iterations) + "ms")
        return durationMs
    }
}
