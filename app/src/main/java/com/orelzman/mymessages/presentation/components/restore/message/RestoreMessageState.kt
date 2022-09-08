package com.orelzman.mymessages.presentation.components.restore.message

import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.presentation.components.util.CRUDResult

/**
 * @author Orel Zilberman
 * 08/09/2022
 */
data class RestoreMessageState(
    val deletedMessages: List<Message> = emptyList(),
    val result: CRUDResult<Message>? = null
)