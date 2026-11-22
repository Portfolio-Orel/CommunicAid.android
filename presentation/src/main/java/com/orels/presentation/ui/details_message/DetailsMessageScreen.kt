package com.orels.presentation.ui.details_message

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import com.orels.presentation.ui.components.filledFieldColors
import com.orels.presentation.ui.components.save_button.SaveButton
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.orels.domain.model.entities.Folder
import com.orels.presentation.R
import com.orels.presentation.ui.components.restore.restore_button.RestoreButton
import com.orels.presentation.ui.components.restore.restore_button.RestoreType
import com.orels.presentation.ui.components.util.SnackbarController
import com.orels.presentation.ui.delete_button.DeleteButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsMessageScreen(
    navController: NavController,
    viewModel: DetailsMessageViewModel = hiltViewModel(),
    messageId: String? = null
) {
    val context = LocalContext.current
    val state = viewModel.state
    val snackbarController = SnackbarController.getInstance()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = messageId) {
        viewModel.setEdit(messageId = messageId)
    }
    LaunchedEffect(key1 = state.eventMessage) {
        when (state.eventMessage) {
            EventsMessages.Saved -> Toast.makeText(
                context,
                context.getString(R.string.message_saved_successfully),
                Toast.LENGTH_LONG
            ).show()
            EventsMessages.Updated -> Toast.makeText(
                context,
                context.getString(R.string.message_updated_successfully),
                Toast.LENGTH_LONG
            ).show()
            EventsMessages.Deleted -> {
                val result = snackbarController.showSnackbar(
                    message = context.getString(R.string.message_deleted_successfully),
                    actionLabel = context.getString(R.string.cancel),
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.undoDelete()
                } else {
                    navController.navigateUp()
                }
            }
            EventsMessages.Restored -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.messages_restored_successfully),
                    Toast.LENGTH_LONG
                ).show()
                navController.navigateUp()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                value = state.title,
                onValueChange = { viewModel.setTitle(it) },
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = stringResource(R.string.title), style = MaterialTheme.typography.bodyLarge)
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = filledFieldColors(isError = state.emptyFields.contains(MessageFields.Body)),
                isError = state.emptyFields.contains(MessageFields.Body)
            )

            Column {
                TextField(
                    value = state.shortTitle,
                    onValueChange = { viewModel.setShortTitle(it) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(R.string.message_symbol), style = MaterialTheme.typography.bodyLarge)
                    },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = filledFieldColors(isError = state.emptyFields.contains(MessageFields.Body)),
                    isError = state.emptyFields.contains(MessageFields.Body)
                )
            }
            Dropdown(
                folders = state.folders, onSelected = { viewModel.setSelectedFolder(it) },
                isError = state.emptyFields.contains(MessageFields.Folder),
                selected = state.selectedFolder ?: Folder()
            )

            TextField(
                value = state.body,
                onValueChange = { viewModel.setBody(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 650.dp),
                placeholder = {
                    Text(text = stringResource(R.string.message), style = MaterialTheme.typography.bodyLarge)
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                maxLines = 30,
                shape = MaterialTheme.shapes.medium,
                colors = filledFieldColors(isError = state.emptyFields.contains(MessageFields.Body)),
                isError = state.emptyFields.contains(MessageFields.Body)
            )
        }
        if (state.isEdit) {
            DeleteButton(
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isLoadingDelete,
                deleteText = R.string.delete_message
            ) {
                viewModel.deleteMessage()
            }
        } else {
            RestoreButton(restoreType = RestoreType.Message)
        }
        if (state.error != R.string.empty_string) {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = stringResource(id = state.error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(Modifier.weight(1f))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SaveButton(
                onClick = { viewModel.saveMessage() },
                isLoading = state.isLoading,
            )
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.large,
                onClick = { navController.navigateUp() },
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun Dropdown(
    modifier: Modifier = Modifier,
    folders: List<Folder>,
    onSelected: (Folder) -> Unit,
    isError: Boolean = false,
    selected: Folder = Folder()
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedFolder by remember { mutableStateOf(selected) }

    LaunchedEffect(key1 = selected) {
        selectedFolder = selected
    }

    val chevronRotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)
    val fieldShape = MaterialTheme.shapes.medium
    val isPlaceholder = selectedFolder.title == ""
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .clip(fieldShape)
                .background(
                    color = if (isError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .then(
                    if (isError) Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.error,
                        shape = fieldShape
                    ) else Modifier
                )
                .clickable {
                    expanded = expanded != true
                }
                .padding(start = 16.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (isPlaceholder) stringResource(R.string.folder) else selectedFolder.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = if (isPlaceholder) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Icon(
                modifier = Modifier.rotate(chevronRotation),
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = stringResource(R.string.expansion_button),
                tint = if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = 400.dp)
                .fillMaxWidth(0.9f)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            folders.forEach {
                DropdownMenuItem(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    text = {
                        Text(
                            text = it.title,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (it.id == selectedFolder.id && !isPlaceholder) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onSelected(it)
                        selectedFolder = it
                        expanded = false
                    })
            }
        }
    }
}