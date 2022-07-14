package com.orelzman.mymessages.presentation.main.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ActionButton(
    onClick: () -> Unit = {},
    isPrimary: Boolean = true,
    text: String,
    isLoading: Boolean = false,
) {
    Button(
        modifier = Modifier
            .padding(start = 32.dp, bottom = 32.dp)
            .width(148.dp)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) MaterialTheme.colorScheme.primary else Color.Transparent,
        ),
        onClick = onClick,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp),
                color = if(isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = if(isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}