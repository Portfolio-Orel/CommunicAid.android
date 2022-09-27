package com.orelzman.mymessages.domain.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.orelzman.mymessages.domain.managers.phonecall.interactor.PhoneCallManagerInteractor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author Orel Zilberman
 * 17/09/2022
 */

@HiltWorker
class EndCallWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val phoneCallManagerInteractor: PhoneCallManagerInteractor
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        CoroutineScope(Dispatchers.Main).launch {
            delay(END_CALL_DELAY_MILLIS)
            phoneCallManagerInteractor.hangupCall()
        }
        return Result.success()
    }

    companion object {
        const val END_CALL_DELAY_MILLIS = 2000L
    }
}