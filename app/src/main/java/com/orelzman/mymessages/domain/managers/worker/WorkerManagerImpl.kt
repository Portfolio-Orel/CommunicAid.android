package com.orelzman.mymessages.domain.managers.worker

import android.content.Context
import androidx.work.*
import com.orelzman.mymessages.domain.workers.UploadWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkerManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : WorkerManager {

    override fun startWorker(type: WorkerType) {
        when (type) {
            WorkerType.UploadCalls -> startPeriodicUploadWorker()
        }
    }

    private fun startPeriodicUploadWorker() =
        queuePeriodicWorker(worker = buildPeriodicUploadWorker(), type = WorkerType.UploadCalls)


    private fun queuePeriodicWorker(
        worker: PeriodicWorkRequest,
        type: WorkerType,
    ) =
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(type.name, ExistingPeriodicWorkPolicy.REPLACE, worker)

    private fun queueOneTimeWorker(
        worker: OneTimeWorkRequest,
        type: WorkerType
    ) = WorkManager.getInstance(context)
        .enqueueUniqueWork(type.name, ExistingWorkPolicy.REPLACE, worker)

    private fun buildPeriodicUploadWorker(): PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<UploadWorker>(
            repeatInterval = UploadWorkerIntervalTime,
            repeatIntervalTimeUnit = UploadWorkerIntervalTimeUnit,
            flexTimeInterval = UploadWorkerFlexibleTime,
            flexTimeIntervalUnit = UploadWorkerFlexibleTimeUnit
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(Tag)
            .build()

    private fun buildOneTimeUploadWorker(): OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(Tag)
            .build()

    companion object {
        const val Tag = "UploadWorkersTag"

        const val UploadWorkerIntervalTime: Long = 15
        val UploadWorkerIntervalTimeUnit = TimeUnit.MINUTES

        const val UploadWorkerFlexibleTime: Long = 5
        val UploadWorkerFlexibleTimeUnit = TimeUnit.MINUTES

    }
}