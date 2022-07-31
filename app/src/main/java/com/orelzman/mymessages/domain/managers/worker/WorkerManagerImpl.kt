package com.orelzman.mymessages.domain.managers.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.work.*
import com.orelzman.mymessages.domain.workers.UploadWorker
import com.orelzman.mymessages.util.extension.Log
import com.orelzman.mymessages.util.extension.toUUIDOrNull
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private val Context.workersSharedPreferences: SharedPreferences
    get() = getSharedPreferences(
        "sp_workers", Context.MODE_PRIVATE
    )

class WorkerManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : WorkerManager {

    private val queuedWorkers: ArrayList<WorkerType> = ArrayList()

    override fun startWorker(type: WorkerType, startImmediately: Boolean) {
        when (type) {
            WorkerType.UploadCalls -> startUploadWorker()
        }
        if(startImmediately) {
            startUploadWorkerOnce(type = type)
        }
    }

    private fun startUploadWorkerOnce(type: WorkerType) {
        val worker = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(Constraints.NONE)
            .addTag(Tag)
            .build()
        Log.v("Started worker immediately $type")
        WorkManager.getInstance(context).enqueue(worker)
    }

    private fun startUploadWorker() {
        val worker =
            PeriodicWorkRequestBuilder<UploadWorker>(
                repeatInterval = UploadWorkerIntervalTime,
                repeatIntervalTimeUnit = UploadWorkerIntervalTimeUnit,
                flexTimeInterval = UploadWorkerFlexibleTime,
                flexTimeIntervalUnit = UploadWorkerFlexibleTimeUnit
            )
        queuePeriodicWorker(workerBuilder = worker, type = WorkerType.UploadCalls)
    }

    private fun queuePeriodicWorker(
        workerBuilder: WorkRequest.Builder<PeriodicWorkRequest.Builder, PeriodicWorkRequest>,
        type: WorkerType,
    ) {
        val worker = workerBuilder
            .setConstraints(Constraints.NONE)
            .addTag(Tag)
            .build()
        val workerInQueueId = getWorkerId(type)
        if (workerInQueueId != null) {
            if (!cancelWorker(id = workerInQueueId, type = type)) {
                Log.v("Failed to cancel worker! $workerInQueueId, type: ${type.name}")
                return
            }
        }
        startPeriodicWorker(
            worker = worker,
            type = type
        )
    }

    private fun startPeriodicWorker(worker: PeriodicWorkRequest, type: WorkerType) {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(type.name, ExistingPeriodicWorkPolicy.KEEP, worker)
        Log.v("Started worker: ${type.name}")
        with(context.workersSharedPreferences.edit()) {
            putString(type.name, worker.id.toString())
            apply()
        }
    }

    private fun cancelWorker(id: String, type: WorkerType): Boolean {
        val uuid = id.toUUIDOrNull() ?: return false
        WorkManager.getInstance(context).cancelWorkById(uuid)
        Log.v("Cancelled worker: ${type.name}")
        with(context.workersSharedPreferences.edit()) {
            putString(type.name, null)
            apply()
        }
        return true
    }

    private fun getWorkerId(type: WorkerType): String? =
        context.workersSharedPreferences.getString(type.name, null)

    companion object {
        const val Tag = "UploadWorkersTag"

        const val UploadWorkerIntervalTime: Long = 15
        val UploadWorkerIntervalTimeUnit = TimeUnit.MINUTES

        const val UploadWorkerFlexibleTime: Long = 5
        val UploadWorkerFlexibleTimeUnit = TimeUnit.MINUTES

    }
}