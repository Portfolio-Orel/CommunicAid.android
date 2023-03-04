package com.orels.presentation.ui.main.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orels.domain.model.entities.Message
import com.orels.presentation.R
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
    addNewMessage: () -> Unit,
    spaceBetweenMessages: Dp,
    borderColor: Color,
    messageHeight: Dp,
    messageWidth: Dp,
    isLoading: Boolean = false,
) {

    val listState = rememberLazyGridState()
    val fabVisibility by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    if (isLoading) {
        LazyVerticalGrid(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(1F)
                .heightIn(max = 2000.dp),
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(spaceBetweenMessages),
            verticalArrangement = Arrangement.spacedBy(spaceBetweenMessages)
        ) {
            items(12) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkeletonComponent(
                        modifier = Modifier
                            .border(
                                shape = MaterialTheme.shapes.medium,
                                width = 1.dp,
                                color = Color.Transparent
                            )
                            .clip(MaterialTheme.shapes.medium)
                            .padding(0.dp),
                        height = messageHeight * 0.7f,
                        width = messageWidth
                    )
                    SkeletonComponent(
                        modifier = Modifier
                            .border(
                                shape = MaterialTheme.shapes.medium,
                                width = 1.dp,
                                color = Color.Transparent
                            )
                            .clip(MaterialTheme.shapes.medium),
                        height = messageHeight * 0.1f,
                        width = messageWidth * 0.95f
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(1F)
                    .heightIn(max = 2000.dp),
                columns = GridCells.Fixed(4),
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(spaceBetweenMessages),
                verticalArrangement = Arrangement.spacedBy(spaceBetweenMessages)
            ) {
                items(
                    count = messages.size,
                    key = { index ->
                        index
                    }
                ) { index ->
                    MessageView(
                        message = messages[index],
                        modifier = Modifier
                            .height(messageHeight)
                            .width(messageWidth)
                            .padding(0.dp),
                        borderColor = borderColor,
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
                }
            }
            AddNewMessageFab(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp),
                onClick = addNewMessage,
                visible = fabVisibility
            )
        }
    }
}

@Composable
private fun AddNewMessageFab(
    onClick: () -> Unit,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = slideInVertically {
            with(density) { 40.dp.roundToPx() }
        } + fadeIn(),
        exit = fadeOut(
            animationSpec = keyframes {
                this.durationMillis = 120
            }
        )
    ) {
        FloatingActionButton(
            onClick = onClick,
            shape = RoundedCornerShape(50),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            content = { Icon(Icons.Filled.Add, stringResource(id = R.string.add_message)) }
        )
    }
}