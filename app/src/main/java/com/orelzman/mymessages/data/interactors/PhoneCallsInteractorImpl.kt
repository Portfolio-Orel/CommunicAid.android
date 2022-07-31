package com.orelzman.mymessages.data.interactors

import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.model.entities.MessageSent
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.domain.model.entities.UploadState
import com.orelzman.mymessages.domain.model.entities.createPhoneCallBodyList
import com.orelzman.mymessages.domain.repository.Repository
import com.orelzman.mymessages.util.extension.addUniqueWithPredicate
import java.util.*
import javax.inject.Inject

class PhoneCallsInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : PhoneCallsInteractor {
    private val db = database.phoneCallDao

    override suspend fun createPhoneCalls(phoneCalls: List<PhoneCall>) {
        if (phoneCalls.isEmpty()) return
        repository.createPhoneCalls(phoneCalls.createPhoneCallBodyList())
    }

    override fun cachePhoneCall(phoneCall: PhoneCall) =
        db.insert(phoneCall)

    override fun cachePhoneCalls(phoneCalls: List<PhoneCall>) =
        db.insert(phoneCalls)

    override fun addMessageSent(phoneCall: PhoneCall, messageSent: MessageSent) {
        db.get(phoneCall.id)?.let { it ->
            val messages = ArrayList(it.messagesSent)
            messages.addUniqueWithPredicate(messageSent) { it1 -> it1.messageId == messageSent.messageId }

            it.messagesSent = messages
            db.update(it)
        }
    }

    override fun updateCall(phoneCall: PhoneCall) =
        db.update(phoneCall)

    override fun updateCallUploadState(phoneCall: PhoneCall, uploadState: UploadState) {
        phoneCall.setUploadState(uploadState)
        db.update(phoneCall)
    }

    override fun getAll(): List<PhoneCall> =
        db.getAll()

    override fun getAllFromDate(fromDate: Date): List<PhoneCall> =
        db.getAllFromDate(fromDate)


    override fun remove(phoneCalls: List<PhoneCall>) = db.remove(phoneCalls)
    override fun clear() = db.clear()
}
