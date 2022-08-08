package com.orelzman.mymessages.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.model.entities.SettingsType
import com.orelzman.mymessages.presentation.settings.components.ToggleSettings

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.settings),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            state.settingsList.forEach { settings ->
                when (settings.key.type) {
                    SettingsType.Toggle -> ToggleSettings(
                        settings = settings,
                        onChecked = viewModel::settingsChecked,
                        checked = settings.value.toBooleanStrictOrNull() ?: false
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ComposablePreview() {
    SettingsScreen()
}