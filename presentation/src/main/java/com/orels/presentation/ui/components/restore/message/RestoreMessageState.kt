package com.orels.presentation.ui.components.restore.message

import com.orels.domain.model.entities.Message
import com.orels.presentation.ui.components.util.CRUDResult

/**
 * @author Orel Zilberman
 * 08/09/2022
 */
data class RestoreMessageState(
    val deletedMessages: List<Message> = emptyList(),
    val result: CRUDResult<Message>? = null
)