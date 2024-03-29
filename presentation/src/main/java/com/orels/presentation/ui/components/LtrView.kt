package com.orels.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * @author Orel Zilberman
 * 13/08/2022
 */

@Composable
fun LtrView(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider() {
        content()
    }
}