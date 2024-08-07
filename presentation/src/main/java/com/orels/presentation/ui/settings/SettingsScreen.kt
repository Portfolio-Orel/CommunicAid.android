package com.orels.presentation.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orels.domain.model.entities.SettingsKey
import com.orels.domain.model.entities.SettingsType
import com.orels.domain.util.permission.PermissionState
import com.orels.presentation.R
import com.orels.presentation.ui.components.OnLifecycleEvent
import com.orels.presentation.ui.components.SkeletonComponent
import com.orels.presentation.ui.settings.components.DataSettings
import com.orels.presentation.ui.settings.components.ToggleSettings
import com.orels.presentation.ui.settings.components.send_sms_settings.SendSMSSettings

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    val context = LocalContext.current

    OnLifecycleEvent(onResume = viewModel::onResume)

    Column(
        modifier = Modifier
            .fillMaxHeight(0.8f)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = String.format(stringResource(id = R.string.hello_name), state.user?.firstName),
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 24.dp, top = 12.dp),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = state.environment?.name ?: "")
            if (state.isLoadingSettings) {
                repeat(4) {
                    SkeletonComponent(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 8.dp)
                            .border(
                                shape = MaterialTheme.shapes.small,
                                width = 1.dp,
                                color = Color.Transparent
                            )
                            .clip(MaterialTheme.shapes.small)
                            .height(24.dp)
                            .width(240.dp),
                    )
                }
            } else {
                state.settingsList.filter { it.key.visibleToUser }
                    .forEach { settings ->
                        when (settings.key.type) {
                            SettingsType.Toggle -> ToggleSettings(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                settings = settings,
                                onChecked = viewModel::settingsChanged,
                                isLoading = state.loadingSettings.contains(settings.key),
                                checked = settings.getRealValue() ?: false,
                                enabled = {
                                    settings.isEnabled() && settings.getPermissionsNotGranted(
                                        context = context
                                    )
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
                                    it.getPermissionsNotGranted(context = context)
                                        .forEach { permission ->
                                            when (permission.getPermissionState(context = context)) {
                                                PermissionState.Granted -> {}
                                                PermissionState.NotAsked -> permission.requestPermission(
                                                    context = context
                                                )

                                                PermissionState.DeniedOnce -> permission.requestPermission(
                                                    context = context
                                                ) // Show rationale
                                                PermissionState.DeniedPermanently -> permission.requestPermission(
                                                    context = context
                                                ) // show dialog to open settings
                                            }
                                        }
                                }
                            )

                            SettingsType.Data -> {
                                DataSettings(
                                    title = stringResource(
                                        settings.key.title
                                            ?: R.string.empty_string
                                    ),
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
        }
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .clickable {
                        viewModel.clearPhonecalls()
                        Toast.makeText(context,
                            context.getString(R.string.calls_cleared), Toast.LENGTH_SHORT).show()
                    },
                text = stringResource(R.string.clear_calls),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoadingSignOut) {
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
                            viewModel.logout()
                        },
                    text = stringResource(id = R.string.logout),
                    style = MaterialTheme.typography.bodyMedium,
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