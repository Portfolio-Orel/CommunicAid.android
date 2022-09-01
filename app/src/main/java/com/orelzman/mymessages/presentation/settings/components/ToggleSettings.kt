package com.orelzman.mymessages.presentation.settings.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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

/**
 * @param contentIfCheck is the content to show if the toggle is pressed and is set to true.
 * It receives a function that is called on dismiss.
 * @param onDisabledClick is called if the settings is disabled and clicked.
 */
@Composable
fun ToggleSettings(
    settings: Settings,
    onChecked: (Settings) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    contentIfCheck: @Composable (() -> Unit)? = null,
    onDisabledClick: (Settings) -> Unit = {}
) {
    var checkedState by remember { mutableStateOf(checked) }

    Row(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .noRippleClickable {
                if (!enabled) {
                    onDisabledClick(settings)
                    return@noRippleClickable
                }
                checkedState = !checkedState
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
            checked = checkedState,
            onCheckedChange = {
                checkedState = !checkedState
                onChecked(settings)
            },
            enabled = enabled
        )
    }
    if (contentIfCheck != null && checked) {
        var visible by remember { mutableStateOf(false) }
        val icon =
            if (visible) painterResource(id = R.drawable.ic_arrow_up) else painterResource(id = R.drawable.ic_arrow_down)
        Column {
            Icon(
                painter = icon,
                contentDescription = stringResource(R.string.show_hide_settings),
                modifier = Modifier
                    .padding(8.dp)
                    .noRippleClickable {
                    visible = !visible
                }
            )
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { -40 })
                        + fadeIn(initialAlpha = 0.3f),
                exit = slideOutVertically() + fadeOut()
            ) {
                Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                    contentIfCheck()
                }
            }
        }
    }
}