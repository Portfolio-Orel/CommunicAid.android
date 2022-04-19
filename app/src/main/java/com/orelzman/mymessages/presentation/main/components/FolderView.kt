package com.orelzman.mymessages.presentation.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orelzman.mymessages.data.dto.Folder


@Composable
fun FolderView(folder: Folder, isSelected: Boolean, modifier: Modifier = Modifier) {
    Box(modifier =
    modifier
        .fillMaxSize()
        .padding(8.dp)
        .background(if(isSelected) {
            Color.Blue
        } else {
            Color.White
        }),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = folder.folderTitle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}