package com.orels.presentation.ui.delete_button

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun DeleteButton(
    isLoading: Boolean,
    @StringRes deleteText: Int,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit = {},
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .height(16.dp)
                .width(16.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.error,
        )
    } else {
        Text(
            modifier = modifier.clickable {
                onDelete()
            },
            text = stringResource(deleteText),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}