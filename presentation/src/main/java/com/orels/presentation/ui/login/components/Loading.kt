package com.orels.presentation.ui.login

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Loading(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    size: Dp = 24.dp,
    width: Dp = 2.dp
) {
    CircularProgressIndicator(
        modifier = modifier
            .size(size),
        color = color,
        strokeWidth = width
    )
}