package com.orelzman.mymessages.domain.service.phone_call

import com.orelzman.mymessages.data.dto.PhoneCall
import kotlinx.coroutines.flow.StateFlow

interface PhoneCallManagerInteractor {
    val numberOnTheLine: StateFlow<PhoneCall?>
}