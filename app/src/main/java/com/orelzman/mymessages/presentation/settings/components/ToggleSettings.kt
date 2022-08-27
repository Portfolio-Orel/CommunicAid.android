package com.orelzman.mymessages.presentation.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.util.extension.noRippleClickable

/**
 * @author Orel Zilberman
 * 08/08/2022
 */

@Composable
fun ToggleSettings(
    settings: Settings,
    onChecked: (Settings) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onDisabledClick: (Settings) -> Unit = {}
) {
    val checkedState = remember { mutableStateOf(checked) }
    Row(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .noRippleClickable {
                if (!enabled) {
                    onDisabledClick(settings)
                    return@noRippleClickable
                }
                checkedState.value = !checkedState.value
                onChecked(settings)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(0.85f),
            text = stringResource(id = settings.key.title ?: R.string.empty_string),
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) MaterialTheme.colorScheme.onBackground
            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = !checkedState.value
                onChecked(settings)
            },
            enabled = enabled
        )
    }
}