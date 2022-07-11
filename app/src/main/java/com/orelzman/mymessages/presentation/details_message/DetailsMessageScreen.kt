package com.orelzman.mymessages.presentation.details_message

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.presentation.main.components.ActionButton
import com.orelzman.mymessages.ui.theme.MyMessagesTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun DetailsMessageScreen(
    navigator: DestinationsNavigator,
    viewModel: DetailsMessageViewModel = hiltViewModel(),
    messageId: String?
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


    MyMessagesTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.setTitle(it) },
                modifier = Modifier
                    .padding(16.dp)
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
                        .padding(16.dp)
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
                    .padding(16.dp)
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = stringResource(R.string.message))
                },
                maxLines = 7,
                isError = state.emptyFields.contains(MessageFields.Body)
            )
            Dropdown(folders = state.folders, onSelected = { viewModel.setFolderId(it) })
            Spacer(modifier = Modifier.weight(1f))

            Row {
                ActionButton(
                    text = stringResource(R.string.save),
                    isLoading = state.isLoading,
                    onClick = { viewModel.saveMessage() },
                )
                Spacer(modifier = Modifier.weight(1f))
                ActionButton(
                    isPrimary = false,
                    text = stringResource(R.string.cancel),
                    onClick = { navigator.navigateUp() },
                )
            }
        }
    }
}

@Composable
fun Dropdown(
    folders: List<Folder>,
    onSelected: (String) -> Unit,
    selectedFolder: Folder = Folder()
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedFolder.title) }
    var textfieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown


    Column(Modifier.padding(20.dp)) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {
                selectedText = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    textfieldSize = coordinates.size.toSize()
                },
            label = { Text(stringResource(R.string.title)) },
            trailingIcon = {
                Icon(icon, stringResource(R.string.expansion_button),
                    Modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textfieldSize.width.toDp() })
        ) {
            folders.forEach { folder ->
                DropdownMenuItem(
                    text = { Text(text = folder.title) },
                    onClick = {
                        selectedText = folder.title
                        onSelected(folder.id)
                        expanded = false
                    })
            }
        }
    }
}