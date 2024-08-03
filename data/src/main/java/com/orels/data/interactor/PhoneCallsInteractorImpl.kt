package com.orels.data.interactor

import com.orels.data.local.LocalDatabase
import com.orels.domain.interactors.PhoneCallsInteractor
import com.orels.domain.model.dto.body.create.createPhoneCallBodyList
import com.orels.domain.model.entities.MessageSent
import com.orels.domain.model.entities.PhoneCall
import com.orels.domain.model.entities.UploadState
import com.orels.domain.repository.Repository
import com.orels.domain.util.extension.addUniqueIf
import java.util.Date
import javax.inject.Inject

class PhoneCallsInteractorImpl @Inject constructor(
    private val repository: Repository,
    database: LocalDatabase,
) : PhoneCallsInteractor {
    private val db = database.phoneCallDao

    override suspend fun createPhoneCalls(phoneCalls: List<PhoneCall>): List<String> {
        if (phoneCalls.isEmpty()) return emptyList()
        return repository.createPhoneCalls(phoneCalls.createPhoneCallBodyList())
    }

    override fun cachePhoneCall(phoneCall: PhoneCall) {
        val phoneCallsFromDb = db.getAll()
        if (phoneCallsFromDb.any { it.startDate == phoneCall.startDate }) return
        db.insert(phoneCall)
    }

    override fun cachePhoneCalls(phoneCalls: List<PhoneCall>) {
        val phoneCallsFromDb = db.getAll()
        if (phoneCallsFromDb.any { phoneCalls.map { it.startDate }.contains(it.startDate) }) return
        db.insert(phoneCalls)
    }

    override fun addMessageSent(phoneCall: PhoneCall, messageSent: MessageSent) {
        db.get(phoneCall.id)?.let { it ->
            val messages = ArrayList(it.messagesSent)
            messages.addUniqueIf(messageSent) { it1 -> it1.messageId == messageSent.messageId }

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

    override fun deletePhoneCalls(phoneCalls: List<PhoneCall>) {
        db.delete(phoneCalls)
    }


    override fun remove(phoneCalls: List<PhoneCall>) = db.remove(phoneCalls)
    override fun clear() = db.clear()
    override fun clearToDate(date: Date) {
        db.clearToDate(date)
    }
}
