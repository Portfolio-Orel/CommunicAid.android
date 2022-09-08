package com.orelzman.mymessages.presentation.components.restore.message

import com.orelzman.mymessages.presentation.components.restore.restore_button.RestoreType

/**
 * @author Orel Zilberman
 * 08/09/2022
 */
data class RestoreMessageState(
    val isLoading: Boolean = false,
    val type: RestoreType = RestoreType.Message
)