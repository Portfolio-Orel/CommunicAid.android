package com.orelzman.mymessages.domain.service

import android.content.Context
import kotlinx.coroutines.flow.Flow


class PhoneCallManagerImpl: PhoneCallManager {

//    override val state: Flow<CallState>

    override fun onIdleState(number: String, context: Context) {

    }

    override fun onRingingState(number: String, context: Context) {

    }

    override fun onOffHookState(number: String, context: Context) {
    }
}