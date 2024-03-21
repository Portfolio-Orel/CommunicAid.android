package com.orels.presentation.ui.settings.components.send_sms_settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.orels.presentation.R
import com.orels.presentation.ui.components.Input

/**
 * @author Orel Zilberman
 * 28/08/2022
 */

@Composable
fun SendSMSSettings(
    viewModel: SendSMSSettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state

    Column {
        Input(
            minLines = 4,
            maxLines = 6,
            placeholder = stringResource(R.string.sms_to_background_call_placeholder),
            initialText = state.smsText,
            onTextChange = viewModel::onSMSTextChange,
            trailingIcon = {
                Box(
                modifier = Modifier
                    .zIndex(2f)
                    .fillMaxHeight()
                    .background(Color.Transparent)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                    if(state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .height(32.dp)
                                .width(32.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            modifier = Modifier
                                .size(32.dp),
                            imageVector = Icons.Filled.Done,
                            contentDescription = stringResource(R.string.done_icon),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
            }
            }
        )
        Row {
//            SaveButton(onClick = { viewModel.saveSMSText(onDismiss) }, isLoading = state.isLoading)
//            ActionButton(
//                modifier = Modifier
//                    .width(120.dp)
//                    .height(48.dp),
//                isPrimary = false,
//                text = stringResource(R.string.cancel),
//                onClick = onDismiss,
//            )
        }
    }
}