package com.orels.presentation.ui.main.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isPrimary: Boolean = true,
    text: String,
    isLoading: Boolean = false,
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) MaterialTheme.colorScheme.primary else Color.Transparent,
        ),
        onClick = onClick,
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .zIndex(2f),
                contentAlignment = Alignment.Center
            )
            {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp),
                    color = if (isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                    strokeWidth = 2.dp
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth()
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color =
                if (isLoading) Color.Transparent
                else if (isPrimary) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}