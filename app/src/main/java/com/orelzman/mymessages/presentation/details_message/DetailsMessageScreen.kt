package com.orelzman.mymessages.presentation.details_message

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.data.dto.Folder
import com.orelzman.mymessages.presentation.destinations.MainScreenDestination
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
    LaunchedEffect(key1 = messageId) {
        viewModel.setEdit(messageId = messageId)
    }
    val state = viewModel.state

    if (state.isMessageSaved) {
        navigator.navigate(MainScreenDestination)
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
            )
            Column {
                OutlinedTextField(
                    value = state.shortTitle,
                    onValueChange = { viewModel.setShortTitle(it) },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    placeholder = {
                        Text(text = "כותרת קצרה")
                    },
                )
//                Text(
//                    "מקסימום 3 תווים",
//                    modifier = Modifier
//                        .size(16.dp)
//                )
            }
            OutlinedTextField(
                value = state.body,
                onValueChange = { viewModel.setBody(it) },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = "טקסט")
                },
                maxLines = 30,
                isError = state.body == ""
            )

            Dropdown(folders = state.folders, onSelected = { viewModel.setFolderId(it) })
            Spacer(modifier = Modifier.weight(1f))

            Row {
                Button(
                    onClick = { viewModel.saveMessage() },
                    modifier = Modifier.padding(start = 32.dp, bottom = 32.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(bottom = 12.dp),
                            color = Color.White
                        )
                    } else {
                        Text("שמור")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { navigator.navigate(MainScreenDestination) },
                    modifier = Modifier.padding(end = 32.dp, bottom = 32.dp)
                ) {
                    Text("בטל")
                }
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
    var selectedText by remember { mutableStateOf(selectedFolder.folderTitle) }
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
            label = { Text("Label") },
            trailingIcon = {
                Icon(icon, "contentDescription",
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
                    text = { Text(text = folder.folderTitle) },
                    onClick = {
                        selectedText = folder.folderTitle
                        onSelected(folder.id)
                        expanded = false
                    })
            }
        }
    }
}