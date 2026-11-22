package com.orels.presentation.ui.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orels.domain.model.entities.Settings
import com.orels.presentation.R
import com.orels.presentation.theme.noRippleClickable
import com.orels.presentation.ui.components.OnLifecycleEvent


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
    enabled: () -> Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    contentIfCheck: @Composable (() -> Unit)? = null,
    onDisabledClick: (Settings) -> Unit = {}
) {
    var checkedState by remember { mutableStateOf(checked) }
    var disabled by remember { mutableStateOf(!enabled()) }

    OnLifecycleEvent(onResume = {
        disabled = !enabled()
    })

    Row(
        modifier = modifier
            .heightIn(min = 56.dp)
            .fillMaxWidth()
            .noRippleClickable {
                if (!disabled) {
                    checkedState = !checkedState
                    onChecked(settings)
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            text = stringResource(id = settings.key.title ?: R.string.empty_string),
            style = MaterialTheme.typography.bodyLarge,
            color = if (disabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            overflow = TextOverflow.Ellipsis
        )
        if (!disabled) {
            Switch(
                checked = checkedState,
                onCheckedChange = {
                    checkedState = !checkedState
                    onChecked(settings)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    checkedBorderColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                ),
                thumbContent = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(3.dp),
                            color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            strokeWidth = 1.5.dp
                        )
                    } else {
                        Spacer(modifier = Modifier)
                    }
                },
                enabled = true
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .noRippleClickable {
                        onDisabledClick(settings)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = stringResource(
                        R.string.ask_permissions
                    ),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
    if (contentIfCheck != null && checked) {
        var visible by remember { mutableStateOf(false) }
        val icon =
            if (visible) painterResource(id = R.drawable.ic_arrow_up) else painterResource(id = R.drawable.ic_arrow_down)
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .noRippleClickable {
                        visible = !visible
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = stringResource(R.string.show_hide_settings),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { -40 })
                        + fadeIn(initialAlpha = 0.3f),
                exit = slideOutVertically() + fadeOut()
            ) {
                Box(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                    contentIfCheck()
                }
            }
        }
    }
}