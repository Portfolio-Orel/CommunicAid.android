package com.orels.presentation.ui.details_folder

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.orels.presentation.R
import com.orels.presentation.ui.components.filledFieldColors
import com.orels.presentation.ui.components.restore.restore_button.RestoreButton
import com.orels.presentation.ui.components.restore.restore_button.RestoreType
import com.orels.presentation.ui.components.save_button.SaveButton
import com.orels.presentation.ui.components.util.SnackbarController
import com.orels.presentation.ui.delete_button.DeleteButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsFolderScreen(
    navController: NavController,
    viewModel: DetailsFolderViewModel = hiltViewModel(),
    folderId: String? = null
) {
    val state = viewModel.state
    val context = LocalContext.current
    val snackbarController = SnackbarController.getInstance()

    LaunchedEffect(key1 = folderId) {
        viewModel.setEdit(folderId)
    }
    LaunchedEffect(key1 = state.eventFolder) {
        when (state.eventFolder) {
            EventsFolder.Saved -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.folder_saved_successfully),
                    Toast.LENGTH_LONG
                ).show()
            }

            EventsFolder.Updated -> Toast.makeText(
                context,
                context.getString(R.string.folder_updated_successfully),
                Toast.LENGTH_LONG
            ).show()
            EventsFolder.Deleted -> {
                val result = snackbarController.showSnackbar(
                    message = context.getString(R.string.folder_deleted_successfully),
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
            EventsFolder.Restored -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.folder_restored_successfully),
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
            colors = filledFieldColors(isError = state.emptyFields.contains(FolderFields.Title)),
            isError = state.emptyFields.contains(FolderFields.Title)
        )
        if (state.isEdit) {
            DeleteButton(
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isLoadingDelete,
                deleteText = R.string.delete_folder
            ) {
                viewModel.deleteFolder()
            }
        } else {
            RestoreButton(restoreType = RestoreType.Folder)
        }
        Spacer(Modifier.weight(1f))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SaveButton(
                onClick = { viewModel.onSaveClick() },
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
