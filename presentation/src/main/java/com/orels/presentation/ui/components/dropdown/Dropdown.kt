package com.orels.presentation.ui.components.dropdown

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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
        Spacer(modifier = Modifier.width(8.dp))
        leadingIcon()

        when (dropdownDecoratorStyle) {
            DropdownDecoratorStyle.Text -> DropdownDecoratorText(
                selected = selectedItem,
                onClick = { expanded = expanded != true },
                defaultTitle = defaultTitle,
                color = color
            )

            DropdownDecoratorStyle.NoBorder -> DropdownDecoratorDefault(
                selected = selectedItem,
                onClick = { expanded = expanded != true },
                defaultTitle = defaultTitle,
                border = false,
                color = color
            )

            else -> DropdownDecoratorDefault(
                selected = selectedItem,
                onClick = { expanded = expanded != true },
                defaultTitle = defaultTitle
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = 400.dp)
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.background
                )
                .clickable { onClick() }
        ) {
            items.forEach {
                DropdownMenuItem(
                    modifier = Modifier
                        .padding(horizontal = 18.dp)
                        .clickable { },
                    text = {
                        Text(
                            text = it.getValue(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    onClick = {
                        onSelected(it)
                        selectedItem = it
                        expanded = false
                    },
                    leadingIcon = {},
                    trailingIcon = {
                        if (secondaryIcon != null) {
                            Icon(
                                imageVector = secondaryIcon,
                                contentDescription = stringResource(R.string.secondary_action),
                                modifier = Modifier.clickable {
                                    secondaryAction(it)
                                    expanded = false
                                }
                            )
                        }
                    },
                )
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
        style = MaterialTheme.typography.bodyMedium,
        color = if (isError) MaterialTheme.colorScheme.error
        else color
    )
}

@Composable
private fun <T : DropdownItem> DropdownDecoratorDefault(
    selected: T?,
    onClick: () -> Unit,
    @StringRes defaultTitle: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    expanded: Boolean = false,
    isError: Boolean = false,
    border: Boolean = true,
) {

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val borderModifier = if (border) {
        modifier.border(
            width = 1.dp,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer,
            shape = RoundedCornerShape(5.dp)
        )
    } else {
        modifier
    }
    Row(
        modifier = borderModifier
            .height(56.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .weight(1f),
            text = selected?.getValue() ?: stringResource(id = defaultTitle),
            style = MaterialTheme.typography.headlineSmall,
            color = if (isError) MaterialTheme.colorScheme.error else color
        )
        Spacer(Modifier.weight(1f))
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.expansion_button),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

enum class DropdownDecoratorStyle {
    Default,
    NoBorder,
    Text,
}