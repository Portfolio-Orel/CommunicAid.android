package com.orels.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.orels.domain.interactors.*
import com.orels.domain.util.common.DateUtils
import com.orels.domain.util.common.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * @author Orel Zilberman
 * 19/08/2022
 */
@HiltWorker
class UploadNotUploadedObjectsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val folderInteractor: FolderInteractor,
    private val messageInteractor: MessageInteractor,
    private val phoneCallsInteractor: PhoneCallsInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val deletedCallsInteractor: DeletedCallsInteractor
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Logger.v("Upload not uploaded worker called")
        CoroutineScope(SupervisorJob()).launch {
            try {
                checkDeletedCalls()
                // TODO("Think of a way to store data of update/create/delete action")
//                checkFolders()
//                checkMessages()
                checkSettings()
                Logger.v("Upload not uploaded done")
            } catch (e: Exception) {
                Logger.e("Upload not uploaded failed with an error: ${e.message ?: e.localizedMessage}")
            }
        }
        return Result.success()
    }

    private suspend fun checkDeletedCalls() {
        val deletedCalls = deletedCallsInteractor.getAllOnce(DateUtils.getFirstDayOfMonth())
            .filter { it.shouldBeUploaded() }
        if (deletedCalls.isNotEmpty()) {
            Logger.v("Deleted calls not uploaded: $deletedCalls")
            deletedCalls.forEach {
                deletedCallsInteractor.create(it)
            }
        }
    }

    private suspend fun checkFolders() {
        val folders = folderInteractor.getAllOnce().filter { it.shouldBeUploaded() }
        if (folders.isNotEmpty()) {
            Logger.v("Folders not uploaded: $folders")
            folders.forEach {
                folderInteractor.createFolder(folder = it)
            }
        }
    }

    private suspend fun checkPhoneCalls() {
        val phoneCalls = phoneCallsInteractor.getAll().filter { it.shouldBeUploaded() }
        if (phoneCalls.isNotEmpty()) {
            Logger.v("phone calls not uploaded: $phoneCalls")
            phoneCallsInteractor.createPhoneCalls(phoneCalls = phoneCalls)
        }
    }

    private suspend fun checkSettings() {
        val settings = settingsInteractor.getAll().filter { it.shouldBeUploaded() }
        if (settings.isNotEmpty()) {
            Logger.v("settings not uploaded")
            settingsInteractor.createOrUpdate(settings)
        }
    }
}
