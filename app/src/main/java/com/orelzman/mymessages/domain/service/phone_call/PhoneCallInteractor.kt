package com.orelzman.mymessages.domain.service.phone_call

import com.orelzman.mymessages.data.dto.PhoneCall
import kotlinx.coroutines.flow.StateFlow

interface PhoneCallInteractor {
    val numberOnTheLine: StateFlow<PhoneCall?>
}