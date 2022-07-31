package com.orelzman.mymessages.domain.managers.phonecall.interactor

import com.orelzman.mymessages.domain.interactors.CallPreferences
import kotlinx.coroutines.flow.Flow

interface PhoneCallManagerInteractor {
    val callsDataFlow: Flow<CallPreferences>
    val callsData: CallPreferences
}