package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.MessageSent
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.domain.model.entities.UploadState
import java.util.*

interface PhoneCallsInteractor {
    suspend fun createPhoneCalls(userId: String, phoneCalls: List<PhoneCall>)
    fun cachePhoneCall(phoneCall: PhoneCall)
    fun cachePhoneCalls(phoneCalls: List<PhoneCall>)
    fun addMessageSent(phoneCall: PhoneCall, messageSent: MessageSent)
    fun updateCall(phoneCall: PhoneCall)
    fun updateCallUploadState(phoneCall: PhoneCall, uploadState: UploadState)
    fun getAll(): List<PhoneCall>
    fun getAllFromDate(fromDate: Date): List<PhoneCall>
    fun clear()
    fun remove(phoneCalls: List<PhoneCall>)
}