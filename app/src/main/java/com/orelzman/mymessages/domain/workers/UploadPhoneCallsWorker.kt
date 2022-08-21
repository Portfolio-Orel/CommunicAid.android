package com.orelzman.mymessages.domain.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.orelzman.mymessages.domain.interactors.CallLogInteractor
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.model.entities.*
import com.orelzman.mymessages.domain.util.extension.Logger
import com.orelzman.mymessages.domain.util.extension.appendAll
import com.orelzman.mymessages.domain.util.extension.compareToBallPark
import com.orelzman.mymessages.domain.util.extension.log
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

@HiltWorker
class UploadPhoneCallsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val phoneCallsInteractor: PhoneCallsInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val callLogInteractor: CallLogInteractor,
) : Worker(context, workerParams) {

    companion object {
        var isRunning: AtomicBoolean = AtomicBoolean(false)
    }

    override fun doWork(): Result {
        try {
            if(isRunning.get()) {
                Logger.v("Worker already running")
            }
            isRunning.set(true)
            Logger.v("Upload phone calls worker called")
            uploadCalls()
        } catch (e: Exception) {
            Logger.e("Upload phone calls worker failed with an error: ${e.message ?: e.localizedMessage}")
            Result.failure()
        }
        return Result.success()
    }

    /**
     * Uploads calls that were registered and ones that were not but are in the call log
     */
    private fun uploadCalls() {
        var phoneCalls = emptyList<PhoneCall>()
        val uploadJob = CoroutineScope(Dispatchers.IO).async {
            phoneCalls = phoneCallsInteractor
                .getAll()
                .distinctBy { it.startDate }
                .filter {
                    it.uploadState == UploadState.NotUploaded
                }
                .appendAll(checkCallsNotRecordedFromCallLog())
                .mapNotNull {
                    it.setUploadState(UploadState.BeingUploaded)
                    phoneCallsInteractor.updateCallUploadState(
                        it,
                        UploadState.BeingUploaded
                    )
                    callLogInteractor.update(it)
                }
            if (phoneCalls.isNotEmpty()) {
                Logger.v("phone calls to upload: $phoneCalls")
                phoneCallsInteractor.createPhoneCalls(
                    phoneCalls
                )
                phoneCalls.forEach { call ->
                    phoneCallsInteractor.updateCallUploadState(call, UploadState.Uploaded)
                }
            }
        }
        CoroutineScope(SupervisorJob()).launch {
            try {
                uploadJob.await()
            } catch (e: Exception) {
                e.log(phoneCalls)
                Logger.e("Worker failed, reason: $e")
                phoneCalls.forEach {
                    phoneCallsInteractor.updateCallUploadState(
                        it,
                        uploadState = UploadState.NotUploaded
                    )
                }
            } finally {
                Logger.v("Upload phone calls worker done.")
                isRunning.set(false)
            }
        }
    }

    private suspend fun checkCallsNotRecordedFromCallLog(): List<PhoneCall> {
        val phoneCalls = ArrayList<PhoneCall>()
        val lastUpdateAt = settingsInteractor.getSettings(SettingsKey.CallsUpdateAt)?.value
        val date = Date(lastUpdateAt?.toLongOrNull() ?: Date().time)
        val potentiallyMissedPhoneCalls =
            callLogInteractor.getCallLogsByDate(startDate = date).toPhoneCalls()
        val savedPhoneCalls = phoneCallsInteractor.getAll()
        potentiallyMissedPhoneCalls.forEach { potentiallyMissedPhoneCall ->
            if (savedPhoneCalls.none {
                    it.number == potentiallyMissedPhoneCall.number
                            && it.startDate.compareToBallPark(potentiallyMissedPhoneCall.startDate)
                }) {
                phoneCalls.add(potentiallyMissedPhoneCall)
            }
        }
        phoneCallsInteractor.cachePhoneCalls(phoneCalls)
        updateCallsUpdateTime()
        return phoneCalls
    }

    private suspend fun updateCallsUpdateTime() {
        settingsInteractor.createOrUpdate(
            Settings(
                key = SettingsKey.CallsUpdateAt, value = Date().time.toString()
            )
        )
    }
}