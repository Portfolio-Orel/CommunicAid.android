package com.orelzman.mymessages.domain.service.phone_call

import com.orelzman.mymessages.domain.interactors.CallPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhoneCallManagerInteractorImpl @Inject constructor(
    private val phoneCallManager: PhoneCallManager
) : PhoneCallManagerInteractor {
    override val callsDataFlow: Flow<CallPreferences>
        get() = phoneCallManager.callsDataFlow
    override val callsData: CallPreferences
        get() = phoneCallManager.callsData
}