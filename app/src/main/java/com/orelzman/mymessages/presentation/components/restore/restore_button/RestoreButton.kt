package com.orelzman.mymessages.presentation.components.restore.restore_button

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.model.entities.Folder
import com.orelzman.mymessages.domain.model.entities.Message
import com.orelzman.mymessages.domain.util.extension.noRippleClickable
import com.orelzman.mymessages.presentation.components.restore.folder.RestoreFolderViewModel
import com.orelzman.mymessages.presentation.components.restore.message.RestoreMessageViewModel
import com.orelzman.mymessages.presentation.components.util.CRUDResult

/**
 * @author Orel Zilberman
 * 07/09/2022
 */

typealias Compose = @Composable () -> Unit

@Suppress("RemoveExplicitTypeArguments") // It's a m
@Composable
fun RestoreButton(
    restoreType: RestoreType,
    modifier: Modifier = Modifier,
) {
    var shouldShowContent by remember { mutableStateOf(false) }
    var content by remember { mutableStateOf<Compose>({}) }

    content = when (restoreType) {
        RestoreType.Folder -> {
            { RestoreFolder() }
        }
        RestoreType.Message -> {
            { RestoreMessage() }
        }
    }

    if (shouldShowContent) {
        Dialog(onDismissRequest = {
            shouldShowContent = false
        }) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .heightIn(min = 100.dp, max = 400.dp)
                    .background(MaterialTheme.colorScheme.onBackground)
            ) {
                content()
            }
        }
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            modifier = Modifier
                .noRippleClickable {
                    shouldShowContent = true
                },
            text = stringResource(R.string.restore_folder),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun RestoreFolder(
    modifier: Modifier = Modifier,
    viewModel: RestoreFolderViewModel = hiltViewModel()
) {
    val state = viewModel.state
    if (state.deletedFolders.isNotEmpty()) {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.deletedFolders) { folder ->
                FolderDetails(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    folder = folder,
                    restore = viewModel::restore,
                    isLoading = state.result is CRUDResult.Loading && state.result.data?.id == folder.id,
                    messagesInFolder = viewModel.getFoldersMessages(folderId = folder.id)
                )
            }
        }
    } else {
        Text(
            text = stringResource(R.string.no_deleted_folders),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun RestoreMessage(
    viewModel: RestoreMessageViewModel = hiltViewModel()
) {

}

@Composable
fun FolderDetails(
    folder: Folder,
    messagesInFolder: List<Message>,
    restore: (Folder) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var showDetails by remember { mutableStateOf(false) }

    Row(modifier = modifier) {
        Column(modifier = modifier) {
            Text(
                modifier = Modifier.noRippleClickable {
                    showDetails = !showDetails
                },
                text = folder.title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.background
            )
            if (showDetails) {
                Box(
                    modifier = modifier
                        .animateContentSize()
                ) {
                    if (messagesInFolder.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .fillMaxHeight(0.3f)
                                .fillMaxWidth()
                        ) {
                            messagesInFolder.forEach {
                                Text(
                                    text = it.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.background
                                )
                            }
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.dash),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.background
                        )
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .height(24.dp)
                    .width(24.dp),
                strokeWidth = 1.dp,
                color = MaterialTheme.colorScheme.background
            )
        } else {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        restore(folder)
                    },
                painter = painterResource(id = R.drawable.ic_round_restore),
                contentDescription = stringResource(
                    R.string.restore
                ),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

enum class RestoreType {
    Folder,
    Message
}