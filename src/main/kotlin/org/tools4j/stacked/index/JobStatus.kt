package org.tools4j.stacked.index

import java.lang.UnsupportedOperationException


interface JobStatus{
    fun onComplete()
    fun onStart()
    fun addOperation(operation: String)
    var running: Boolean
    var currentOperationProgress: String
}

class NullJobStatus: JobStatus{
    override var running: Boolean = false
        get() = throw UnsupportedOperationException()

    override var currentOperationProgress: String = ""
        get() = throw UnsupportedOperationException()

    override fun addOperation(operation: String) {
        throw UnsupportedOperationException()
    }

    override fun onComplete() {
        throw UnsupportedOperationException()
    }

    override fun onStart() {
        throw UnsupportedOperationException()
    }
}

class JobStatusImpl: JobStatus {
    override var running: Boolean = true
    override var currentOperationProgress: String = ""
    private val operationHistory = ArrayList<String>()

    override fun toString(): String {
        if(operationHistory.isEmpty()) return ""
        val sb = StringBuilder()
        sb.append("=============================================================================\n")
        sb.append(operationHistory.joinToString("\n")).append("\n")
        if(currentOperationProgress.isNotEmpty()) {
            sb.append("-----------------------------------------------------------------------------\n")
            sb.append(currentOperationProgress).append("\n")
        }
        sb.append("=============================================================================\n")
        return sb.toString()
    }

    override fun addOperation(s: String) {
        operationHistory.add(s)
    }

    override fun onStart() {
        Thread({
            while(running){
                Thread.sleep(1000)
                println(this.toString())
            }
        }).start()
    }

    override fun onComplete(){
        running = false
    }
}

interface JobSupplier {
    fun get(): JobStatus?;
}

class JobContainer: JobSupplier {
    var jobStatus: JobStatus = NullJobStatus()

    override fun get(): JobStatus {
        return jobStatus
    }

    fun createNew(): JobStatus {
        jobStatus = JobStatusImpl()
        jobStatus!!.onStart();
        return jobStatus!!
    }
}