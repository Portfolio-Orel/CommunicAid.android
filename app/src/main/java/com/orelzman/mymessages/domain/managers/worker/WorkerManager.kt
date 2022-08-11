package com.orelzman.mymessages.domain.managers.worker

import androidx.work.Operation

interface WorkerManager {
    fun startWorker(type: WorkerType)
    fun clearAll(): Operation
}

enum class WorkerType {
    UploadCalls,
    UploadCallsOnce;
}