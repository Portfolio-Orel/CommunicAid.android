package com.orels.presentation.ui.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.orels.domain.model.entities.Folder
import com.orels.presentation.R
import com.orels.presentation.ui.components.dropdown.Dropdown
import com.orels.presentation.ui.components.dropdown.DropdownDecoratorStyle

/**
 * @author Orel Zilberman
 * 09/09/2022
 */

@Composable
fun FoldersContainer(
    folders: List<Folder>,
    onClick: (Folder) -> Unit,
    onEditClick: (Folder) -> Unit,
    onDropdownClick: () -> Unit,
    addNewFolder: () -> Unit,
    selected: Folder?,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        FloatingActionButton(
            modifier = Modifier.size(30.dp),
            onClick = addNewFolder,
            shape = RoundedCornerShape(10),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            content = { Icon(Icons.Filled.Add, stringResource(id = R.string.add_folder)) }
        )
        Spacer(modifier = Modifier.weight(0.4f))
        Dropdown(
            items = folders,
            onSelected = onClick,
            secondaryAction = onEditClick,
            secondaryIcon = Icons.Rounded.Edit,
            defaultTitle = R.string.empty_string,
            selected = selected,
            color = color,
            onClick = onDropdownClick,
            dropdownDecoratorStyle = DropdownDecoratorStyle.Text
        )
        Spacer(modifier = Modifier.weight(0.5f))
    }
}