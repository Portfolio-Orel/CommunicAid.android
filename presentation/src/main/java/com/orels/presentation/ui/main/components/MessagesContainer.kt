package com.orels.presentation.ui.main.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orels.domain.model.entities.Message
import com.orels.presentation.R

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
    height: Dp,
    width: Dp
) {

    val listState = rememberLazyGridState()
    val fabVisibility by derivedStateOf {
        listState.firstVisibleItemIndex == 0
    }

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
                    // Return a stable + unique key for the item
                    index
                }
            ) { index ->
                MessageView(
                    message = messages[index],
                    modifier = Modifier
                        .height(height)
                        .width(width)
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