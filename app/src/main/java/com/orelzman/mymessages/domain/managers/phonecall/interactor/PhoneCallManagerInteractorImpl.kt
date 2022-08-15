package com.orelzman.mymessages.domain.managers.phonecall.interactor

import com.orelzman.mymessages.domain.interactors.CallPreferences
import com.orelzman.mymessages.domain.managers.phonecall.PhoneCallManager
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