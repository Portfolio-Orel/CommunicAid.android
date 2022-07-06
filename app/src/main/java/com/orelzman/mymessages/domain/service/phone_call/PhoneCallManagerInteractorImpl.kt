package com.orelzman.mymessages.domain.service.phone_call

import com.orelzman.mymessages.domain.model.entities.PhoneCall
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PhoneCallManagerInteractorImpl @Inject constructor(
    private val phoneCallManager: PhoneCallManager
) : PhoneCallManagerInteractor {

    override val numberOnTheLine: StateFlow<PhoneCall?>
        get() = phoneCallManager.callOnTheLine
    override val callInBackground: StateFlow<PhoneCall?>
        get() = phoneCallManager.callInBackground
}