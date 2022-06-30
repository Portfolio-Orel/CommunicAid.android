package com.orelzman.mymessages.data.local.interactors.phoneCall

import com.orelzman.mymessages.data.dto.PhoneCall
import com.orelzman.mymessages.data.dto.createPhoneCallBodyList
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.remote.repository.api.Repository
import javax.inject.Inject

class PhoneCallsInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : PhoneCallsInteractor {
    private val db = database.phoneCallDao

    override suspend fun addPhoneCalls(userId: String, phoneCalls: List<PhoneCall>) {
        try {
            repository.createPhoneCalls(phoneCalls.createPhoneCallBodyList(userId))
        } catch (exception: Exception) {
        }
    }

    override fun cachePhoneCall(phoneCall: PhoneCall) =
        db.insert(phoneCall)

    override fun addMessageSent(phoneCall: PhoneCall, messageId: String) {
        val messages = ArrayList(phoneCall.messagesSent)
        messages.add(messageId)
        phoneCall.messagesSent = messages
        db.insert(phoneCall)
    }

    override fun getAll(): List<PhoneCall> =
        db.getAll()

}
