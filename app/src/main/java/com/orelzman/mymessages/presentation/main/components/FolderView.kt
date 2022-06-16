package com.orelzman.mymessages.presentation.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orelzman.mymessages.data.dto.Folder


@Composable
fun FolderView(
    folder: Folder,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (Folder) -> Unit,
    onLongClick: (Folder) -> Unit = {}
) {
    Box(
        modifier =
        modifier
            .fillMaxSize()
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.background
                }
            ).clickable { onClick(folder) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = folder.folderTitle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}