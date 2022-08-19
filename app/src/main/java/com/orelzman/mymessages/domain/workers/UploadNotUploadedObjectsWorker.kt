package com.orelzman.mymessages.domain.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.orelzman.mymessages.domain.interactors.*
import com.orelzman.mymessages.domain.util.common.DateUtils
import com.orelzman.mymessages.domain.util.extension.Logger
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
    private val messageInFolderInteractor: MessageInFolderInteractor,
    private val phoneCallsInteractor: PhoneCallsInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val deletedCallsInteractor: DeletedCallsInteractor
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        try {
            Logger.v("Upload not uploaded worker called")
            CoroutineScope(SupervisorJob()).launch {
                checkDeletedCalls()
                checkFolders()
                checkMessages()
                checkPhoneCalls()
                checkSettings()
                Logger.v("Upload not uploaded done")
            }
        } catch (e: Exception) {
            Logger.e("Upload not uploaded failed with an error: ${e.message ?: e.localizedMessage}")
            return Result.failure()
        }
        return Result.success()
    }

    private suspend fun checkDeletedCalls() {
        val deletedCalls = deletedCallsInteractor.getAllOnce(DateUtils.getFirstDayOfMonth())
            .filter { it.shouldBeUploaded() }
        if (deletedCalls.isNotEmpty()) {
            deletedCalls.forEach {
                deletedCallsInteractor.create(it)
            }
        }
    }

    private suspend fun checkFolders() {
        val folders = folderInteractor.getAllOnce().filter { it.shouldBeUploaded() }
        if (folders.isNotEmpty()) {
            folders.forEach {
                folderInteractor.createFolder(folder = it)
            }
        }
    }

    private suspend fun checkMessages() {
        val messages = messageInteractor.getAllOnce().filter { it.shouldBeUploaded() }
        if (messages.isNotEmpty()) {
            messages.forEach {
                messageInFolderInteractor.getMessageFolderId(it.id)?.let { folderId ->
                    messageInteractor.createMessage(
                        message = it,
                        folderId = folderId
                    )
                }
            }
        }
    }

    private suspend fun checkPhoneCalls() {
        val phoneCalls = phoneCallsInteractor.getAll().filter { it.shouldBeUploaded() }
        if (phoneCalls.isNotEmpty()) {
            phoneCallsInteractor.createPhoneCalls(phoneCalls = phoneCalls)
        }
    }

    private suspend fun checkSettings() {
        val settings = settingsInteractor.getAll().filter { it.shouldBeUploaded() }
        if (settings.isNotEmpty()) {
            settings.forEach {
                settingsInteractor.createOrUpdate(it)
            }
        }
    }
}
