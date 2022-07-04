package com.orelzman.mymessages.data.local.interactors.phoneCall

import com.orelzman.mymessages.domain.model.entities.MessageSent
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.domain.model.entities.createPhoneCallBodyList
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.repository.Repository
import javax.inject.Inject

class PhoneCallsInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : PhoneCallsInteractor {
    private val db = database.phoneCallDao

    override suspend fun addPhoneCalls(userId: String, phoneCalls: List<PhoneCall>) {
        repository.createPhoneCalls(phoneCalls.createPhoneCallBodyList(userId))
    }

    override fun cachePhoneCall(phoneCall: PhoneCall) =
        db.insert(phoneCall)

    override fun addMessageSent(phoneCall: PhoneCall, messageSent: MessageSent) {
        val phoneCallFromDb = db.getByStartDate(phoneCall.startDate)
        val messages = ArrayList(phoneCallFromDb.messagesSent)
        messages.add(messageSent)
        phoneCallFromDb.messagesSent = messages
        db.update(phoneCallFromDb)
    }

    override fun updateCall(phoneCall: PhoneCall) =
        db.update(phoneCall)


    override fun getAll(): List<PhoneCall> =
        db.getAll()

    override fun clear() = db.clear()
    override fun remove(phoneCalls: List<PhoneCall>) = db.remove(phoneCalls)

}
