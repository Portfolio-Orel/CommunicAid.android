package com.orelzman.mymessages.presentation.components.charts.model


import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White

data class DonutItem(
    val title: @Composable ColumnScope.() -> Unit,
    val textInside: @Composable BoxScope.() -> Unit,
    val outerSize: Float = 85f,
    val innerSize: Float = 90f,
    val outerColor: Color = Red,
    val innerColor: Color = White,
)