package com.orels.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.orels.presentation.R

/**
 * @author Orel Zilberman
 * 13/09/2022
 */

typealias Compose = @Composable () -> Unit

object Images {
    @Composable
    fun NoMessagesInFolder(wrapper: @Composable (Compose) -> Unit) {
        wrapper {
            if (isSystemInDarkTheme()) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_empty_folder_dark),
                    contentDescription = stringResource(R.string.empty_string)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_empty_folder_light),
                    contentDescription = stringResource(R.string.empty_string)
                )
            }
        }
    }
}