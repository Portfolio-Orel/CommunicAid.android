package com.orelzman.mymessages.domain.service.phone_call

import com.orelzman.mymessages.data.dto.PhoneCall
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PhoneCallInteractorImpl @Inject constructor(
    private val phoneCallManager: PhoneCallManager
) : PhoneCallInteractor {

    override val numberOnTheLine: StateFlow<PhoneCall?>
        get() = phoneCallManager.callOnTheLine

}