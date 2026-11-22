package com.orels.presentation.ui.main.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.orels.domain.model.entities.Folder
import com.orels.presentation.R
import com.orels.presentation.ui.components.SkeletonComponent
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
    isLoading: Boolean = false
) {
    if (isLoading) {
        SkeletonComponent(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(MaterialTheme.shapes.medium)
                .fillMaxWidth()
                .height(52.dp),
        )
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Dropdown(
                modifier = Modifier.fillMaxWidth(),
                items = folders,
                onSelected = onClick,
                secondaryAction = onEditClick,
                secondaryIcon = Icons.Rounded.Edit,
                defaultTitle = R.string.empty_string,
                selected = selected,
                color = MaterialTheme.colorScheme.onSurface,
                onClick = onDropdownClick,
                dropdownDecoratorStyle = DropdownDecoratorStyle.NoBorder,
            )
        }
    }
}
