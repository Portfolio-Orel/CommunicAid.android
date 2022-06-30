package com.orelzman.mymessages.domain.service.phone_call

import com.orelzman.mymessages.data.dto.PhoneCall
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PhoneCallManagerInteractorImpl @Inject constructor(
    private val phoneCallManager: PhoneCallManager
) : PhoneCallManagerInteractor {

    override val numberOnTheLine: StateFlow<PhoneCall?>
        get() = phoneCallManager.callOnTheLine
}