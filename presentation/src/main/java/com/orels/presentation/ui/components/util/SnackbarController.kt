package com.orels.presentation.ui.components.util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.mutableStateOf

class SnackbarController private constructor() {
    val snackbarHostState = mutableStateOf(SnackbarHostState())

    companion object {
        private var SnackbarControllerInstance: SnackbarController? = null

        fun getInstance(): SnackbarController {
            if (SnackbarControllerInstance == null) {
                SnackbarControllerInstance = SnackbarController()
            }
            return SnackbarControllerInstance!!
        }
    }

    suspend fun showSnackbar(
        message: String,
        actionLabel: String,
        withDismissAction: Boolean,
        duration: SnackbarDuration = SnackbarDuration.Long,
    ): SnackbarResult =
        snackbarHostState.value.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            withDismissAction = withDismissAction,
            duration = duration
        )
}