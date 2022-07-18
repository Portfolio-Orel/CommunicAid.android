package com.orelzman.mymessages.presentation.details_folder

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.orelzman.mymessages.R
import com.orelzman.mymessages.presentation.main.components.ActionButton

@Composable
fun DetailsFolderScreen(
    navController: NavController,
    viewModel: DetailsFolderViewModel = hiltViewModel(),
    folderId: String? = null
) {
    val state = viewModel.state
    LaunchedEffect(key1 = folderId) {
        viewModel.setEdit(folderId)
    }
    if (state.isFolderAdded) {
        navController.navigateUp()
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
            isError = state.emptyFields.contains(FolderFields.Title)
        )
        Row {
            ActionButton(
                text = stringResource(R.string.save),
                isLoading = state.isLoading,
                onClick = { viewModel.onSaveClick() },
            )
            Spacer(modifier = Modifier.weight(1f))
            ActionButton(
                isPrimary = false,
                text = stringResource(R.string.cancel),
                onClick = { navController.navigateUp() },
            )
        }
    }
}