package com.orels.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.orels.domain.interactors.PhoneCallsInteractor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

/**
 * Created by Orel Zilberman on 22/03/2024.
 */
@HiltWorker
class ClearPhoneCallsCacheWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val phoneCallsInteractor: PhoneCallsInteractor
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        CoroutineScope(Dispatchers.Main).launch {
            val oneWeekAgoDate = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)
            phoneCallsInteractor.clearToDate(oneWeekAgoDate)
        }
        return Result.success()
    }
}