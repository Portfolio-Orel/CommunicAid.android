package com.orelzman.mymessages.domain.service.PhoneCall

import com.orelzman.mymessages.data.dto.PhoneCall
import kotlinx.coroutines.flow.StateFlow

interface PhoneCallInteractor {
    val numberOnTheLine: StateFlow<PhoneCall?>
}