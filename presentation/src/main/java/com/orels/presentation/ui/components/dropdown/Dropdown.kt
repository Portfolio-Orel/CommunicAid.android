package com.orels.presentation.ui.components.dropdown

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orels.domain.model.entities.DropdownItem
import com.orels.presentation.R

/**
 * @author Orel Zilberman
 * 19/08/2022
 */

@Composable
fun <T : DropdownItem> Dropdown(
    items: List<T>,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    secondaryAction: (T) -> Unit = {},
    secondaryIcon: ImageVector? = null,
    @StringRes defaultTitle: Int = R.string.empty_string,
    color: Color = MaterialTheme.colorScheme.onBackground,
    isError: Boolean = false,
    selected: T? = null,
    onClick: () -> Unit = {},
    dropdownDecoratorStyle: DropdownDecoratorStyle = DropdownDecoratorStyle.Default,
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember {
        mutableStateOf(
            selected ?: items.firstOrNull()
        )
    }

    LaunchedEffect(key1 = selected) {
        selectedItem = selected
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        leadingIcon()

        Box(modifier = Modifier.weight(1f, fill = false)) {
            when (dropdownDecoratorStyle) {
                DropdownDecoratorStyle.Text -> DropdownDecoratorText(
                    selected = selectedItem,
                    onClick = { expanded = !expanded },
                    defaultTitle = defaultTitle,
                    color = color,
                    isError = isError,
                )

                DropdownDecoratorStyle.NoBorder -> DropdownDecoratorDefault(
                    selected = selectedItem,
                    onClick = { expanded = !expanded },
                    defaultTitle = defaultTitle,
                    border = false,
                    color = color,
                    expanded = expanded,
                    isError = isError,
                )

                else -> DropdownDecoratorDefault(
                    selected = selectedItem,
                    onClick = { expanded = !expanded },
                    defaultTitle = defaultTitle,
                    expanded = expanded,
                    isError = isError,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .fillMaxWidth(0.9f)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onClick() }
            ) {
                items.forEach {
                    val isSelected = it.getValue() == selectedItem?.getValue()
                    DropdownMenuItem(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else Color.Transparent
                            ),
                        text = {
                            Text(
                                text = it.getValue(),
                                style = MaterialTheme.typography.titleSmall,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onSelected(it)
                            selectedItem = it
                            expanded = false
                        },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null,
                        trailingIcon = if (secondaryIcon != null) {
                            {
                                IconButton(
                                    modifier = Modifier.size(36.dp),
                                    onClick = {
                                        secondaryAction(it)
                                        expanded = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = secondaryIcon,
                                        contentDescription = stringResource(R.string.secondary_action),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        } else null,
                    )
                }
            }
        }
        trailingIcon()
    }
}

@Composable
private fun <T : DropdownItem> DropdownDecoratorText(
    selected: T?,
    onClick: () -> Unit,
    @StringRes defaultTitle: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    isError: Boolean = false,
) {
    Text(
        modifier = modifier.clickable { onClick() },
        text = selected?.getValue() ?: stringResource(id = defaultTitle),
        style = MaterialTheme.typography.titleSmall,
        color = if (isError) MaterialTheme.colorScheme.error else color
    )
}

@Composable
private fun <T : DropdownItem> DropdownDecoratorDefault(
    selected: T?,
    onClick: () -> Unit,
    @StringRes defaultTitle: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    expanded: Boolean = false,
    isError: Boolean = false,
    border: Boolean = true,
) {
    val chevronRotation by animateFloatAsState(if (expanded) 180f else 0f)
    val shape = MaterialTheme.shapes.medium

    val base = modifier
        .height(52.dp)
        .fillMaxWidth()
        .clip(shape)
        .background(MaterialTheme.colorScheme.surfaceVariant)
    val withBorder = if (border || isError) {
        base.border(
            width = 1.dp,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
            shape = shape
        )
    } else base

    Row(
        modifier = withBorder.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = selected?.getValue() ?: stringResource(id = defaultTitle),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (isError) MaterialTheme.colorScheme.error else color
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(30.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = stringResource(R.string.expansion_button),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(20.dp)
                    .rotate(chevronRotation)
            )
        }
    }
}

enum class DropdownDecoratorStyle {
    Default,
    NoBorder,
    Text,
}
