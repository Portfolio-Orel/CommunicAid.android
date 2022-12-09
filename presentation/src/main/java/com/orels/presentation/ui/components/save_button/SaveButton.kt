package com.orels.presentation.ui.components.save_button

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.orels.presentation.R
import com.orels.presentation.theme.noRippleClickable

/**
 * @author Orel Zilberman
 * 28/08/2022
 */

@Composable
fun SaveButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(8.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .height(16.dp)
                    .width(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .noRippleClickable {
                        onClick()
                    },
                text = stringResource(id = R.string.save),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

    }
}