package com.orelzman.mymessages.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.model.entities.SettingsKey
import com.orelzman.mymessages.domain.model.entities.SettingsType
import com.orelzman.mymessages.domain.util.RequiredPermission
import com.orelzman.mymessages.domain.util.extension.Logger
import com.orelzman.mymessages.presentation.components.save_button.SaveButton
import com.orelzman.mymessages.presentation.settings.components.DataSettings
import com.orelzman.mymessages.presentation.settings.components.ToggleSettings
import com.orelzman.mymessages.presentation.settings.components.send_sms_settings.SendSMSSettings

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current



    LaunchedEffect(key1 = state.eventSettings) {
        when (state.eventSettings) {
            EventsSettings.Saved -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.settings_saved_successfully),
                    Toast.LENGTH_LONG
                ).show()
            }
            EventsSettings.Unchanged -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.settings_unchanged),
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.settings),
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 24.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.settingsList.forEach { settings ->
                when (settings.key.type) {
                    SettingsType.Toggle -> ToggleSettings(
                        settings = settings,
                        onChecked = viewModel::settingsChanged,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = settings.getRealValue() ?: false,
                        enabled = settings.isEnabled() && settings.arePermissionsGranted(context = context)
                            .isEmpty() && !state.isLoading,
                        contentIfCheck = if (settings.key == SettingsKey.SendSMSToBackgroundCall) {
                            {
                                SendSMSSettings()
                            }
                        } else {
                            null
                        },
                        onDisabledClick = {
                            val permissionsNotGranted = it.arePermissionsGranted(context = context)
                            permissionsNotGranted.forEach { permission ->
                                val permissionState = permission.isGranted(context = context)
                                if (permissionState == RequiredPermission.PermissionState.DeniedPermanently) {
                                    Logger.v("Permanently not allowed")
                                } else {
                                    permission.requestPermission(context = context)
                                }
                            }
                        }
                    )
                    SettingsType.Data -> {
                        DataSettings(
                            title = stringResource(settings.key.title ?: R.string.empty_string),
                            body = settings.getRealValue<String>().toString(),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                    SettingsType.PopUp -> {

                    }
                    else -> {}
                }
            }
            Spacer(Modifier.weight(1f))
            SaveButton(
                modifier = Modifier
                    .width(120.dp)
                    .height(50.dp),
                isLoading = state.isLoading,
                onClick = viewModel::saveSettings
            )
        }
    }
}

@Preview
@Composable
fun ComposablePreview() {
    SettingsScreen()
}