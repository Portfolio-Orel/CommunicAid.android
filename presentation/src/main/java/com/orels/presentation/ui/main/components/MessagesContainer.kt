package com.orels.presentation.ui.main.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orels.domain.model.entities.Message
import com.orels.presentation.ui.components.SkeletonComponent

/**
 * @author Orel Zilberman
 * 09/09/2022
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessagesContainer(
    messages: List<Message>,
    onClick: (Message) -> Unit,
    onLongClick: (Message, Context) -> Unit,
    spaceBetweenMessages: Dp,
    borderColor: Color,
    messageHeight: Dp,
    messageWidth: Dp,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val listState = rememberLazyGridState()

    if (isLoading) {
        LazyVerticalGrid(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(1F),
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(spaceBetweenMessages),
            verticalArrangement = Arrangement.spacedBy(spaceBetweenMessages),
            contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
        ) {
            items(12) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SkeletonComponent(
                        modifier = Modifier
                            .clip(RoundedCornerShape(22.dp))
                            .height(messageHeight * 0.68f)
                            .width(messageWidth),
                    )
                    SkeletonComponent(
                        modifier = Modifier
                            .clip(CircleShape)
                            .height(12.dp)
                            .width(messageWidth * 0.7f),
                    )
                }
            }
        }
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                columns = GridCells.Fixed(4),
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(spaceBetweenMessages),
                verticalArrangement = Arrangement.spacedBy(spaceBetweenMessages),
                // Leaves room below the last row so it can scroll clear of the FAB + folder pill
                contentPadding = PaddingValues(top = 12.dp, bottom = 160.dp)
            ) {
                items(
                    count = messages.size,
                    key = { index ->
                        messages[index].id
                    }
                ) { index ->
                    MessageView(
                        message = messages[index],
                        modifier = Modifier
                            .animateItemPlacement()
                            .height(messageHeight)
                            .width(messageWidth),
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
                }
            }
        }
    }
}
