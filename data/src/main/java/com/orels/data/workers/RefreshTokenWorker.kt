package com.orels.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.orels.auth.domain.interactor.Auth
import com.orels.domain.util.common.Logger
import com.orels.domain.util.extension.log
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * @author Orel Zilberman
 * 11/09/2022
 */

@HiltWorker
class RefreshTokenWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val auth: Auth
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        CoroutineScope(SupervisorJob()).launch {
            try {
                auth.refreshToken()
                Logger.i("Token refreshed")
            } catch (e: Exception) {
                e.log()
            }
        }
        return Result.success()
    }

}