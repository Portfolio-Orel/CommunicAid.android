package com.orelzman.mymessages.domain.managers.worker

interface WorkerManager {
    fun startWorker(type: WorkerType)
}

enum class WorkerType {
    UploadCalls;
}