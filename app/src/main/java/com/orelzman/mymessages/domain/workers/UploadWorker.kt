package com.orelzman.mymessages.domain.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.util.extension.Log
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val authInteractor: AuthInteractor,
    ) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        try {

        } catch (e: Exception) {
            Log.vCustom(e.message ?: e.localizedMessage)
        }
        return Result.success()
    }
}