package com.orelzman.mymessages.presentation.details_folder

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    LaunchedEffect(key1 = folderId) {
        viewModel.setEdit(folderId)
    }
    LaunchedEffect(key1 = state.isFolderAdded) {
        if (state.isFolderAdded) {
            Toast.makeText(
                context,
                context.getString(R.string.folder_saved_successfully),
                Toast.LENGTH_LONG
            ).show()
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
                onClick = { viewModel.onSaveClick() },
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
                Text(text = stringResource(R.string.title))
            },
            isError = state.emptyFields.contains(FolderFields.Title)
        )
    }
}