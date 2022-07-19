package com.orelzman.mymessages.data.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.model.entities.MessageSent
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.domain.model.entities.UploadState
import com.orelzman.mymessages.domain.model.entities.createPhoneCallBodyList
import com.orelzman.mymessages.domain.repository.Repository
import javax.inject.Inject

class PhoneCallsInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : PhoneCallsInteractor {
    private val db = database.phoneCallDao

    override suspend fun createPhoneCalls(userId: String, phoneCalls: List<PhoneCall>) {
        repository.createPhoneCalls(phoneCalls.createPhoneCallBodyList(userId))
    }

    override fun cachePhoneCall(phoneCall: PhoneCall) =
        db.insert(phoneCall)

    override fun cachePhoneCalls(phoneCalls: List<PhoneCall>) =
        db.insert(phoneCalls)

    override fun addMessageSent(phoneCall: PhoneCall, messageSent: MessageSent) {
        db.get(phoneCall.id)?.let {
            val messages = ArrayList(it.messagesSent)
            messages.add(messageSent)
            it.messagesSent = messages
            db.update(it)
        }
    }

    override fun updateCall(phoneCall: PhoneCall) =
        db.update(phoneCall)

    override fun updateCallUploadState(phoneCall: PhoneCall, uploadState: UploadState) {
        phoneCall.uploadState = uploadState
        db.update(phoneCall)
    }

    override fun getAll(): List<PhoneCall> =
        db.getAll()

    override fun clear() = db.clear()
    override fun remove(phoneCalls: List<PhoneCall>) = db.remove(phoneCalls)

}
