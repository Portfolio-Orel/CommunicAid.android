package com.orels.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.orels.domain.interactors.CallDetailsInteractor
import com.orels.domain.interactors.PhoneCallsInteractor
import com.orels.domain.interactors.SettingsInteractor
import com.orels.domain.model.entities.*
import com.orels.domain.util.common.Constants
import com.orels.domain.util.common.Logger
import com.orels.domain.util.extension.appendAll
import com.orels.domain.util.extension.compareToBallPark
import com.orels.domain.util.extension.log
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import java.util.*

typealias PhoneCalls = List<PhoneCall>

@HiltWorker
class UploadPhoneCallsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val phoneCallsInteractor: PhoneCallsInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val callLogInteractor: CallDetailsInteractor,
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        try {
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
            delay(Constants.TIME_TO_ADD_CALL_TO_CALL_LOG)
            phoneCalls = phoneCallsInteractor
                .getAll()
                .distinctBy { it.startDate }
                .filter {
                    it.uploadState == UploadState.NotUploaded
                            || it.uploadState == UploadState.BeingUploaded
                }
                .appendAll(checkCallsNotRecorded())
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
                Logger.v("Upload phone calls worker done.")
            } catch (e: Exception) {
                e.log(phoneCalls)
                Logger.e("Worker failed, reason: $e")
                phoneCalls.forEach {
                    phoneCallsInteractor.updateCallUploadState(
                        it,
                        uploadState = UploadState.NotUploaded
                    )
                }
            }
        }
    }

    private suspend fun checkCallsNotRecorded(): PhoneCalls {
        val phoneCalls = ArrayList(
            phoneCallsInteractor.getAll().filter { it.uploadState == UploadState.NotUploaded })
        val lastUpdateAt = settingsInteractor.getSettings(SettingsKey.CallsUpdateAt).value
        val date = Date(lastUpdateAt.toLongOrNull() ?: Date().time)
        val potentiallyMissedPhoneCalls =
            callLogInteractor.getCallLogsByDate(startDate = date).toPhoneCalls()
        potentiallyMissedPhoneCalls.forEach { potentiallyMissedPhoneCall ->
            val actualMissedCall = phoneCalls.find {
                it.number == potentiallyMissedPhoneCall.number
                        && it.startDate.compareToBallPark(potentiallyMissedPhoneCall.startDate)
            }
            if (actualMissedCall == null) {
                phoneCalls.add(potentiallyMissedPhoneCall)
            }
        }
        phoneCallsInteractor.cachePhoneCalls(phoneCalls)
        updateCallsUpdateTime()
        return phoneCalls
    }

//    private fun updatePhoneCallsActualTime(phoneCall: PhoneCall) {
//        phoneCall.actualEndDate = Date()
//        phoneCallsInteractor.updateCall(phoneCall = phoneCall)
//    }

    private suspend fun updateCallsUpdateTime() {
        settingsInteractor.createOrUpdate(
            listOf(
                Settings(
                    key = SettingsKey.CallsUpdateAt, value = Date().time.toString()
                )
            )
        )
    }
}