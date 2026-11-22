package com.orels.presentation.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orels.domain.model.entities.SettingsKey
import com.orels.domain.model.entities.SettingsType
import com.orels.domain.util.permission.PermissionState
import com.orels.presentation.R
import com.orels.presentation.theme.AppTheme
import com.orels.presentation.ui.components.OnLifecycleEvent
import com.orels.presentation.ui.components.SkeletonComponent
import com.orels.presentation.ui.settings.components.DataSettings
import com.orels.presentation.ui.settings.components.ToggleSettings
import com.orels.presentation.ui.settings.components.send_sms_settings.SendSMSSettings

private val SettingsCardShape = RoundedCornerShape(24.dp)

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    val context = LocalContext.current

    OnLifecycleEvent(onResume = viewModel::onResume)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.hello_name, state.user?.firstName.orEmpty())
                    .replace(", !", "!"),
                modifier = Modifier.padding(horizontal = 4.dp),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            val environmentName = state.environment?.name ?: ""
            if (environmentName.isNotEmpty()) {
                Text(
                    text = environmentName,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 2.dp,
                        shape = SettingsCardShape,
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    )
                    .clip(SettingsCardShape)
                    .background(AppTheme.extended.cardSurface)
                    .border(1.dp, AppTheme.extended.cardBorder, SettingsCardShape)
                    .padding(vertical = 4.dp)
            ) {
                if (state.isLoadingSettings) {
                    repeat(4) { index ->
                        if (index > 0) SettingsDivider()
                        SkeletonComponent(
                            modifier = Modifier
                                .padding(vertical = 16.dp, horizontal = 16.dp)
                                .clip(MaterialTheme.shapes.small)
                                .height(24.dp)
                                .width(240.dp),
                        )
                    }
                } else {
                    state.settingsList
                        .filter { it.key.visibleToUser }
                        .filter { it.key.type == SettingsType.Toggle || it.key.type == SettingsType.Data }
                        .forEachIndexed { index, settings ->
                            if (index > 0) SettingsDivider()
                            when (settings.key.type) {
                                SettingsType.Toggle -> ToggleSettings(
                                    modifier = Modifier.padding(horizontal = 16.dp),
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
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }

                                else -> {}
                            }
                        }
                }
            }

            Spacer(Modifier.height(20.dp))
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large,
                onClick = {
                    val cleanedCallsCount = viewModel.clearPhonecalls()
                    Toast.makeText(
                        context,
                        context.getString(R.string.calls_cleaned, cleanedCallsCount),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.clear_calls),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        FilledTonalButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 8.dp)
                .height(56.dp),
            shape = MaterialTheme.shapes.large,
            enabled = !state.isLoadingSignOut,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                disabledContentColor = MaterialTheme.colorScheme.onErrorContainer,
            ),
            onClick = { viewModel.logout() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (state.isLoadingSignOut) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.logout),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Preview
@Composable
fun ComposablePreview() {
    SettingsScreen()
}
