package com.orels.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.orels.domain.interactors.PhoneCallsInteractor
import com.orels.domain.util.common.Logger
import com.orels.domain.util.extension.log
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Orel Zilberman on 22/03/2024.
 */
@HiltWorker
class ClearPhoneCallsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val phoneCallsInteractor: PhoneCallsInteractor
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Logger.v("Clear phone calls worker called")
        CoroutineScope(Dispatchers.Main).launch {
            try {

                val currentPhonecalls = phoneCallsInteractor.getAll()
                Logger.v("About to clear ${currentPhonecalls.size} phonecalls")
                phoneCallsInteractor.clear()
                val newPhonecalls = phoneCallsInteractor.getAll()
                Logger.v("${currentPhonecalls.size - newPhonecalls.size} phonecalls were cleared")
            } catch (e: Exception) {
                e.log()
            }
        }
        return Result.success()
    }
}