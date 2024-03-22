package com.orels.domain.managers.worker

interface WorkerManager {
    fun startWorker(type: WorkerType)
    fun clearAll()
}

enum class WorkerType {
    UploadCalls,
    UploadCallsOnce,
    UploadNotUploadedObjectsOnce,
    EndCallOnce,
    ClearPhoneCalls,
    RefreshToken;
}