package com.orels.presentation.ui.main.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
                .border(
                    shape = MaterialTheme.shapes.medium,
                    width = 1.dp,
                    color = Color.Transparent
                )
                .clip(MaterialTheme.shapes.medium)
                .fillMaxWidth(0.8f)
                .height(25.dp),
        )
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .border(
                    shape = MaterialTheme.shapes.medium,
                    width = 1.dp,
                    color = Color.Transparent
                )
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Dropdown(
                modifier = Modifier.fillMaxWidth(),
                items = folders,
                onSelected = onClick,
                secondaryAction = onEditClick,
                secondaryIcon = Icons.Rounded.Edit,
                defaultTitle = R.string.empty_string,
                selected = selected,
                color = color,
                onClick = onDropdownClick,
                dropdownDecoratorStyle = DropdownDecoratorStyle.NoBorder,
                leadingIcon = {
                    FloatingActionButton(
                        modifier = Modifier.size(30.dp),
                        onClick = addNewFolder,
                        shape = RoundedCornerShape(10),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        content = {
                            Icon(
                                Icons.Filled.Add,
                                stringResource(id = R.string.add_folder)
                            )
                        }
                    )
                }
            )
            Spacer(modifier = Modifier.weight(0.6f))
        }
    }
}