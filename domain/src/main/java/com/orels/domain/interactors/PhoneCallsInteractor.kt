package com.orels.domain.interactors

import com.orels.domain.model.entities.MessageSent
import com.orels.domain.model.entities.PhoneCall
import com.orels.domain.model.entities.UploadState
import java.util.*

interface PhoneCallsInteractor {
    suspend fun createPhoneCalls(phoneCalls: List<PhoneCall>)
    fun cachePhoneCall(phoneCall: PhoneCall)
    fun cachePhoneCalls(phoneCalls: List<PhoneCall>)
    fun addMessageSent(phoneCall: PhoneCall, messageSent: MessageSent)
    fun updateCall(phoneCall: PhoneCall)
    fun updateCallUploadState(phoneCall: PhoneCall, uploadState: UploadState)
    fun getAll(): List<PhoneCall>
    fun getAllFromDate(fromDate: Date): List<PhoneCall>
    fun clear()
    fun clearToDate(date: Date)
    fun remove(phoneCalls: List<PhoneCall>)
}