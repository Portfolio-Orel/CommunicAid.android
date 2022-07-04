package com.orelzman.mymessages.domain.service.phone_call

import com.orelzman.mymessages.domain.model.entities.PhoneCall
import kotlinx.coroutines.flow.StateFlow

interface PhoneCallManagerInteractor {
    val numberOnTheLine: StateFlow<PhoneCall?>
}