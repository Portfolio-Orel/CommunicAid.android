package com.orels.presentation.ui.components.restore.restore_button

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

    val restoreTitle = when (restoreType) {
        RestoreType.Message -> stringResource(R.string.restore_message)
        RestoreType.Folder -> stringResource(R.string.restore_folder)
    }

    if (shouldShowContent) {
        Dialog(onDismissRequest = {
            shouldShowContent = false
        }) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = restoreTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 60.dp, max = 400.dp)
                            .padding(horizontal = 12.dp)
                    ) {
                        content()
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { shouldShowContent = false }) {
                            Text(
                                text = stringResource(R.string.cancel),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
    TextButton(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        onClick = { shouldShowContent = true },
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.ic_round_restore),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = restoreTitle,
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.deletedFolders) { folder ->
                ItemDetails(
                    modifier = Modifier,
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
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = stringResource(R.string.dash),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    })
            }
        }
    } else {
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = stringResource(R.string.no_deleted_folders),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.deletedMessages) { message ->
                ItemDetails(
                    modifier = Modifier,
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
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = message.body,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }
    } else {
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = stringResource(R.string.no_deleted_folders),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .animateContentSize()
            .padding(start = 16.dp, end = 8.dp, top = 6.dp, bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .noRippleClickable {
                        showDetails = !showDetails
                    },
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    FilledTonalIconButton(
                        modifier = Modifier.size(40.dp),
                        onClick = { restore(item) },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(id = R.drawable.ic_round_restore),
                            contentDescription = stringResource(
                                R.string.restore
                            ),
                        )
                    }
                }
            }
        }
        if (showDetails) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp, end = 8.dp)
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