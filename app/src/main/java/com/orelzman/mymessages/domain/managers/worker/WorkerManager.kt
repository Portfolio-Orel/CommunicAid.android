package com.orelzman.mymessages.domain.managers.worker

interface WorkerManager {
    fun startWorker(type: WorkerType, startImmediately: Boolean = false)
}

enum class WorkerType {
    UploadCalls,
//    UploadCallsOnce
}