package com.orels.presentation.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import com.orels.domain.model.entities.SettingsKey
import com.orels.domain.model.entities.SettingsType
import com.orels.domain.util.RequiredPermission
import com.orels.domain.util.common.Logger
import com.orels.presentation.R
import com.orels.presentation.ui.components.OnLifecycleEvent
import com.orels.presentation.ui.settings.components.DataSettings
import com.orels.presentation.ui.settings.components.ToggleSettings
import com.orels.presentation.ui.settings.components.send_sms_settings.SendSMSSettings

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current

    OnLifecycleEvent(onResume = viewModel::onResume)

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
            .fillMaxHeight(0.9f)
            .fillMaxWidth()
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
                        modifier = Modifier.padding(horizontal = 8.dp),
                        settings = settings,
                        onChecked = viewModel::settingsChanged,
                        isLoading = state.loadingSettings.contains(settings.key),
                        checked = settings.getRealValue() ?: false,
                        enabled = {
                            settings.isEnabled() && settings.getPermissionsNotGranted(context = context)
                                .isEmpty()
                        },
                        contentIfCheck = if (settings.key == SettingsKey.SendSMSToBackgroundCall) {
                            {
                                SendSMSSettings()
                            }
                        } else {
                            null
                        },
                        onDisabledClick = {
                            it.getPermissionsNotGranted(context = context).forEach { permission ->
                                val permissionState = permission.isGranted(context = context)
                                if (permissionState == RequiredPermission.PermissionState.DeniedPermanently) {
                                    Logger.v("Permanently not allowed") // ToDo
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
        }
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if(state.isLoadingSignOut) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(32.dp)
                        .width(32.dp),
                    strokeWidth = 1.dp,
                    color = MaterialTheme.colorScheme.error,
                )
            } else {
                Text(
                    modifier = Modifier
                        .clickable {
                            viewModel.signOut()
                        },
                    text = stringResource(id = R.string.logout),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview
@Composable
fun ComposablePreview() {
    SettingsScreen()
}