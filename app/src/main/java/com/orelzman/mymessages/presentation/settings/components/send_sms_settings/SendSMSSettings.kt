package com.orelzman.mymessages.presentation.settings.components.send_sms_settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.R
import com.orelzman.mymessages.presentation.login.components.Input

/**
 * @author Orel Zilberman
 * 28/08/2022
 */

@Composable
fun SendSMSSettings(
    onDismiss: () -> Unit,
    viewModel: SendSMSSettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state
    Column {
        Input(
            minLines = 4,
            maxLines = 6,
            placeholder = stringResource(R.string.sms_to_background_call_placeholder),
            onTextChange = viewModel::onSMSTextChange
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