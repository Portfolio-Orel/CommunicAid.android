package com.orelzman.mymessages.domain.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.domain.interactors.AnalyticsInteractor
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.model.entities.*
import com.orelzman.mymessages.util.common.CallUtils
import com.orelzman.mymessages.util.common.Constants
import com.orelzman.mymessages.util.extension.Log
import com.orelzman.mymessages.util.extension.appendAll
import com.orelzman.mymessages.util.extension.compareToBallPark
import com.orelzman.mymessages.util.extension.log
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import java.util.*

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val authInteractor: AuthInteractor,
    private val phoneCallsInteractor: PhoneCallsInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val analyticsInteractor: AnalyticsInteractor
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        try {
            uploadCalls()
        } catch (e: Exception) {
            Log.vCustom(e.message ?: e.localizedMessage)
        }
        return Result.success()
    }

    private fun uploadCalls() {
        var phoneCalls = emptyList<PhoneCall>()
        val uploadJob = CoroutineScope(Dispatchers.IO).async {
            delay(Constants.TIME_TO_ADD_CALL_TO_CALL_LOG)
            phoneCalls = phoneCallsInteractor
                .getAll()
                .appendAll(checkCallsNotRecorded())
                .distinctBy { it.startDate }
                .filter { it.uploadState == UploadState.NotUploaded }
                .mapNotNull {
                    it.uploadState = UploadState.BeingUploaded
                    phoneCallsInteractor.updateCallUploadState(
                        it,
                        UploadState.BeingUploaded
                    )
                    return@mapNotNull CallUtils.update(context, it)
                }
            authInteractor.getUser()?.userId?.let {
                phoneCallsInteractor.createPhoneCalls(
                    it,
                    phoneCalls
                )
                phoneCalls.forEach { call ->
                    phoneCallsInteractor.updateCallUploadState(call, UploadState.Uploaded)
                    analyticsInteractor.track("Call Deleted", "call" to call.number)
                }
            }
        }
        try {
            CoroutineScope(Dispatchers.IO).launch {
                uploadJob.await()
            }
        } catch (e: Exception) {
            e.log(phoneCalls)
            phoneCalls.forEach {
                phoneCallsInteractor.updateCallUploadState(
                    it,
                    uploadState = UploadState.NotUploaded
                )
            }
        }
    }

    private suspend fun checkCallsNotRecorded(): List<PhoneCall> {
        val phoneCalls = ArrayList<PhoneCall>()
        val lastUpdateAt = settingsInteractor.getSettings(SettingsKeys.CallsUpdateAt)?.value
        val date = Date(lastUpdateAt?.toLongOrNull() ?: Date().time)
        val potentiallyMissedPhoneCalls =
            CallUtils.getCallLogsByDate(context, startDate = date).toPhoneCalls()
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
        authInteractor.getUser()?.let {
            settingsInteractor.createSettings(
                Settings(
                    key = SettingsKeys.CallsUpdateAt, value = Date().time.toString()
                ),
                userId = it.userId
            )
        }
        return phoneCalls
    }
}