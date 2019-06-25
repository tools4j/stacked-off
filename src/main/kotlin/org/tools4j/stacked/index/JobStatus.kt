package org.tools4j.stacked.index

import mu.KLogging


interface JobStatus{
    fun onComplete()
    fun onStart()
    fun addOperation(operation: String)
    var running: Boolean
    var currentOperationProgress: String
}

class NullJobStatus : JobStatus{
    override fun onComplete() {
        throw UnsupportedOperationException()
    }

    override fun onStart() {
        throw UnsupportedOperationException()
    }

    override fun addOperation(operation: String) {
        throw UnsupportedOperationException()
    }

    override var running: Boolean
        get() = false
        set(value) {throw UnsupportedOperationException()}

    override var currentOperationProgress: String
        get() = "No current operation..."
        set(value) {throw UnsupportedOperationException()}

}

class JobStatusImpl: JobStatus {
    override var running: Boolean = true
    override var currentOperationProgress: String = ""
    private val operationHistory = ArrayList<String>()
    companion object: KLogging()

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

    override fun addOperation(operation: String) {
        operationHistory.add(operation)
        currentOperationProgress = ""
    }

    override fun onStart() {
        Thread({
            while(running){
                Thread.sleep(1000)
                logger.debug{ this.toString() }
            }
        }).start()
    }

    override fun onComplete(){
        currentOperationProgress = "Complete"
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
        jobStatus.onStart();
        return jobStatus
    }
}