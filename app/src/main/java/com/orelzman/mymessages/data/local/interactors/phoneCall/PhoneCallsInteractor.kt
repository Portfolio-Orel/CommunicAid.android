package com.orelzman.mymessages.data.local.interactors.phoneCall

import com.orelzman.mymessages.data.dto.PhoneCall

interface PhoneCallsInteractor {
    suspend fun addPhoneCalls(userId: String, phoneCalls: List<PhoneCall>)
    fun cachePhoneCall(phoneCall: PhoneCall)
    fun addMessageSent(phoneCall: PhoneCall, messageId: String)
    fun getAll(): List<PhoneCall>
}