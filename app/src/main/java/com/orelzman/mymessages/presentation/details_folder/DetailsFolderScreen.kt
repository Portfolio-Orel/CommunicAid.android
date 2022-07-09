package com.orelzman.mymessages.presentation.details_folder

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun DetailsFolderScreen(
    navigator: DestinationsNavigator,
    viewModel: DetailsFolderViewModel = hiltViewModel(),
    folderId: String?
) {
    val state = viewModel.state
    LaunchedEffect(key1 = folderId) {
        viewModel.setEdit(folderId)
    }
    if (state.isFolderAdded) {
        navigator.navigateUp()
    }
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
                Text(text = stringResource(R.string.title))
            },
            isError =  state.emptyFields.contains(FolderFields.Title)
        )
        Row {
            Button(
                onClick = { viewModel.onSaveClick() },
                modifier = Modifier.padding(start = 32.dp, bottom = 32.dp)
                    .align(Alignment.CenterVertically)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(stringResource(R.string.save))
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { navigator.navigateUp() },
                modifier = Modifier.padding(end = 32.dp, bottom = 32.dp)
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}