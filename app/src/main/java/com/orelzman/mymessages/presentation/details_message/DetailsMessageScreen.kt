package com.orelzman.mymessages.presentation.details_message

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.presentation.main.components.ActionButton

@Composable
fun DetailsMessageScreen(
    navController: NavController,
    viewModel: DetailsMessageViewModel = hiltViewModel(),
    messageId: String? = null
) {
    val context = LocalContext.current
    val state = viewModel.state

    LaunchedEffect(key1 = messageId) {
        viewModel.setEdit(messageId = messageId)
    }
    LaunchedEffect(key1 = viewModel.state.eventMessage) {
        when (state.eventMessage) {
            EventsMessages.MessageSaved -> Toast.makeText(
                context,
                context.getString(R.string.message_saved_successfully),
                Toast.LENGTH_LONG
            ).show()
            else -> {}
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceAround) {
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
        OutlinedTextField(
            value = state.title,
            onValueChange = { viewModel.setTitle(it) },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                Text(text = "כותרת")
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
        OutlinedTextField(
            value = state.body,
            onValueChange = { viewModel.setBody(it) },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(R.string.message))
            },
            maxLines = 7,
            isError = state.emptyFields.contains(MessageFields.Body)
        )
        Dropdown(
            folders = state.folders, onSelected = { viewModel.setSelectedFolder(it) },
            isError = state.emptyFields.contains(MessageFields.Folder),
            selected = state.selectedFolder ?: Folder()
        )
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
            style = MaterialTheme.typography.labelLarge,
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