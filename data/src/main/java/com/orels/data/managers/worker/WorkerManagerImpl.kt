package com.orels.data.managers.worker

import android.content.Context
import androidx.work.*
import com.orels.data.workers.RefreshTokenWorker
import com.orels.data.workers.UploadNotUploadedObjectsWorker
import com.orels.data.workers.UploadPhoneCallsWorker
import com.orels.domain.managers.worker.WorkerManager
import com.orels.domain.managers.worker.WorkerType
import com.orels.data.workers.EndCallWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkerManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : WorkerManager {

    override fun startWorker(type: WorkerType) {
        when (type) {
            WorkerType.UploadCalls -> queuePeriodicWorker(
                worker = buildPeriodicUploadWorker(),
                type = type
            )
            WorkerType.UploadCallsOnce -> queueOneTimeWorker(
                worker = buildOneTimeWorker<UploadPhoneCallsWorker>(),
                type = type
            )
            WorkerType.UploadNotUploadedObjectsOnce -> queueOneTimeWorker(
                worker = buildOneTimeWorker<UploadNotUploadedObjectsWorker>(),
                type = type
            )
            WorkerType.RefreshToken -> queuePeriodicWorker(
                worker = buildPeriodicRefreshTokenWorker(),
                type = type
            )
            WorkerType.EndCallOnce -> queueOneTimeWorker(
                worker = buildOneTimeWorker<EndCallWorker>(),
                type = type
            )
        }
    }

    override fun clearAll() {
        WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
    }

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

    private fun buildPeriodicRefreshTokenWorker(): PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<RefreshTokenWorker>(
            repeatInterval = RefreshTokenWorkerIntervalTime,
            repeatIntervalTimeUnit = RefreshTokenWorkerIntervalTimeUnit,
            flexTimeInterval = RefreshTokenWorkerFlexibleTime,
            flexTimeIntervalUnit = RefreshTokenWorkerFlexibleTimeUnit
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(TAG)
            .build()

    private fun buildPeriodicUploadWorker(): PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<UploadPhoneCallsWorker>(
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
            .addTag(TAG)
            .build()

    private inline fun <reified W : ListenableWorker> buildOneTimeWorker(): OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<W>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(TAG)
            .build()

    companion object {
        const val TAG = "MyMessages_Workers"

        const val UploadWorkerIntervalTime: Long = 15
        const val UploadWorkerFlexibleTime: Long = 5
        val UploadWorkerIntervalTimeUnit = TimeUnit.MINUTES
        val UploadWorkerFlexibleTimeUnit = TimeUnit.MINUTES

        const val RefreshTokenWorkerIntervalTime: Long = 1
        const val RefreshTokenWorkerFlexibleTime: Long = 1
        val RefreshTokenWorkerIntervalTimeUnit = TimeUnit.HOURS
        val RefreshTokenWorkerFlexibleTimeUnit = TimeUnit.HOURS
    }
}