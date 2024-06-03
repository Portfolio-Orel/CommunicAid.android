package com.orels.presentation.ui.details_message

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.orels.presentation.ui.main.components.ActionButton

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
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.setTitle(it) },
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = stringResource(R.string.title))
                },
                isError = state.emptyFields.contains(MessageFields.Body)
            )

            Column {
                OutlinedTextField(
                    value = state.shortTitle,
                    onValueChange = { viewModel.setShortTitle(it) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(R.string.message_symbol))
                    },
                    isError = state.emptyFields.contains(MessageFields.Body)
                )
            }
            Dropdown(
                folders = state.folders, onSelected = { viewModel.setSelectedFolder(it) },
                isError = state.emptyFields.contains(MessageFields.Folder),
                selected = state.selectedFolder ?: Folder()
            )

            OutlinedTextField(
                value = state.body,
                onValueChange = { viewModel.setBody(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 650.dp),
                placeholder = {
                    Text(text = stringResource(R.string.message))
                },
                maxLines = 30,
                isError = state.emptyFields.contains(MessageFields.Body)
            )
        }
        if (state.isEdit) {
            DeleteButton(isLoading = state.isLoadingDelete, deleteText = R.string.delete_message) {
                viewModel.deleteMessage()
            }
        } else {
            RestoreButton(restoreType = RestoreType.Message)
        }
        if (state.error != R.string.empty_string) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(id = state.error),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ActionButton(
                modifier = Modifier
                    .width(120.dp)
                    .height(48.dp),
                text = stringResource(R.string.save),
                isLoading = state.isLoading,
                onClick = { viewModel.saveMessage() },
            )
            Spacer(modifier = Modifier.weight(1f))
            ActionButton(
                modifier = Modifier
                    .width(120.dp)
                    .height(48.dp),
                isPrimary = false,
                text = stringResource(R.string.cancel),
                onClick = { navController.navigateUp() },
            )
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

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    Row(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .clickable {
                expanded = expanded != true
            }
            .border(
                width = 1.dp,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(5.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            if (selectedFolder.title == "") stringResource(R.string.folder) else selectedFolder.title,
            modifier = Modifier
                .padding(horizontal = 18.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(Modifier.weight(1f))
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.expansion_button),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .height(400.dp)
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.background
            )
    ) {
        folders.forEach {
            DropdownMenuItem(
                modifier = Modifier
                    .padding(horizontal = 18.dp),
                text = {
                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
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