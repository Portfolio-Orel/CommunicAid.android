package com.orelzman.mymessages.data.local.interactors.phoneCall

import com.orelzman.mymessages.data.dto.PhoneCall

interface PhoneCallStatisticsInteractor {
    suspend fun addPhoneCalls(userId: String, phoneCalls: List<PhoneCall>)
    fun cachePhoneCall(phoneCall: PhoneCallStatistics)
    fun addMessageSent(phoneCall: PhoneCall, messageId: String)
    fun getAll(): List<PhoneCallStatistics>
}