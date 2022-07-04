package com.orelzman.mymessages.domain.interactors

import com.orelzman.mymessages.domain.model.entities.MessageSent
import com.orelzman.mymessages.domain.model.entities.PhoneCall

interface PhoneCallsInteractor {
    suspend fun addPhoneCalls(userId: String, phoneCalls: List<PhoneCall>)
    fun cachePhoneCall(phoneCall: PhoneCall)
    fun addMessageSent(phoneCall: PhoneCall, messageSent: MessageSent)
    fun updateCall(phoneCall: PhoneCall)
    fun getAll(): List<PhoneCall>
    fun clear()
    fun remove(phoneCalls: List<PhoneCall>)
}