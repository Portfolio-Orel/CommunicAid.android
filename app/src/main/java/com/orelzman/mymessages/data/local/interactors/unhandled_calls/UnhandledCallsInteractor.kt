package com.orelzman.mymessages.data.local.interactors.unhandled_calls

import com.orelzman.mymessages.data.dto.UnhandledCall

interface UnhandledCallsInteractor {

    suspend fun insert(uid: String, unhandledCall: UnhandledCall)

    suspend fun update(uid: String, unhandledCall: UnhandledCall)

    suspend fun getAll(uid: String): List<UnhandledCall>
}