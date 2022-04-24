package com.orelzman.mymessages.domain.service.PhoneCall

import com.orelzman.mymessages.data.dto.PhoneCall
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class PhoneCallInteractorImpl @Inject constructor(
    private val phoneCallManager: PhoneCallManager
) : PhoneCallInteractor {

    override val numberOnTheLine: StateFlow<PhoneCall?>
        get() = phoneCallManager.callOnTheLine.asStateFlow()

}