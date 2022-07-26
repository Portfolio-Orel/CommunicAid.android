package com.orelzman.mymessages.presentation.components.top_app_bar

import com.orelzman.mymessages.domain.model.entities.PhoneCall

data class TopAppBarState(
    val callOnTheLine: PhoneCall? = null
)