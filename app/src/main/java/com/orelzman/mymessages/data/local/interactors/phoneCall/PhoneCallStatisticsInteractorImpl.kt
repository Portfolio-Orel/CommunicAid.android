package com.orelzman.mymessages.data.local.interactors.phoneCall

import com.orelzman.mymessages.data.dto.PhoneCall
import com.orelzman.mymessages.data.dto.PhoneCallStatistics
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.remote.repository.Repository
import javax.inject.Inject

class PhoneCallStatisticsInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : PhoneCallStatisticsInteractor {
    private val db = database.phoneCallDao

    override suspend fun addPhoneCalls(uid: String, phoneCalls: List<PhoneCall>) {
        try {
            repository.addPhoneCalls(uid, phoneCalls.map { it.data })
            db.delete(phoneCalls.map { PhoneCallStatistics(it) })
        } catch (exception: Exception) {
        }
    }

    override fun cachePhoneCall(phoneCall: PhoneCallStatistics) =
        db.insert(phoneCall)

    override fun addMessageSent(phoneCall: PhoneCall, messageId: String) {
        val messages = ArrayList(phoneCall.messagesSent)
        messages.add(messageId)
        phoneCall.messagesSent = messages
        db.insert(PhoneCallStatistics(phoneCall))
    }

    override fun getAll(): List<PhoneCallStatistics> =
        db.getAll()

}
