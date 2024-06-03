package com.orels.presentation.ui.components.restore.restore_button

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.orels.presentation.R
import com.orels.presentation.theme.noRippleClickable
import com.orels.presentation.ui.components.restore.folder.RestoreFolderViewModel
import com.orels.presentation.ui.components.restore.message.RestoreMessageViewModel
import com.orels.presentation.ui.components.util.CRUDResult

/**
 * @author Orel Zilberman
 * 07/09/2022
 */

typealias Compose = @Composable () -> Unit

@Suppress("RemoveExplicitTypeArguments") // It's a must for the content to be a Composable
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
                    .background(MaterialTheme.colorScheme.background)
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
            text = when (restoreType) {
                RestoreType.Message -> stringResource(R.string.restore_message)
                RestoreType.Folder -> stringResource(R.string.restore_folder)
            },
            style = MaterialTheme.typography.bodyMedium,
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
                ItemDetails(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    item = folder,
                    title = folder.title,
                    restore = viewModel::restore,
                    isLoading = state.result is CRUDResult.Loading && state.result.data?.id == folder.id,
                    contentOnExpand =
                    {
                        if (viewModel.getFoldersMessages(folderId = folder.id).isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .fillMaxHeight(0.3f)
                                    .fillMaxWidth()
                            ) {
                                viewModel.getFoldersMessages(folderId = folder.id).forEach {
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
                    })
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
    modifier: Modifier = Modifier,
    viewModel: RestoreMessageViewModel = hiltViewModel()
) {
    val state = viewModel.state

    if (state.deletedMessages.isNotEmpty()) {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.deletedMessages) { message ->
                ItemDetails(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    item = message,
                    title = message.title,
                    restore = viewModel::restore,
                    isLoading = state.result is CRUDResult.Loading && state.result.data?.id == message.id,
                    contentOnExpand = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = viewModel.getMessageFolder(messageId = message.id)?.title
                                    ?: stringResource(
                                        id = R.string.empty_string
                                    ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.4f)
                            )
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = message.body,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.background
                            )
                        }
                    }
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
fun <T> ItemDetails(
    item: T,
    title: String,
    contentOnExpand: Compose,
    restore: (T) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var showDetails by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(modifier = modifier) {
            Text(
                modifier = Modifier.noRippleClickable {
                    showDetails = !showDetails
                },
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.weight(1f))
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(24.dp)
                        .width(24.dp),
                    strokeWidth = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Icon(
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            restore(item)
                        },
                    painter = painterResource(id = R.drawable.ic_round_restore),
                    contentDescription = stringResource(
                        R.string.restore
                    ),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        if (showDetails) {
            Box(
                modifier = modifier
                    .animateContentSize()
            ) {
                contentOnExpand()
            }
        }
    }
}

enum class RestoreType {
    Folder,
    Message
}