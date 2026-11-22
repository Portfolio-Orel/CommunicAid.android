package com.orels.presentation.ui.components.multi_fab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.orels.presentation.theme.noRippleClickable
import com.orels.presentation.R

/**
 * The "+" button, the folders button and the speed-dial actions.
 *
 * They are ALWAYS anchored to the physical right edge of the screen (with "+" being the right-most
 * button), regardless of the device's language direction, so the layout is forced to LTR here.
 * (Only icons are drawn, so there is no text that would need RTL.)
 */
@Composable
fun QuickActionsSpeedDial(
    modifier: Modifier = Modifier,
    folderTitle: String? = null,
    onFoldersClick: (() -> Unit)? = null,
    onAddMessage: () -> Unit = {},
    onAddFolder: () -> Unit = {},
    onUnhandledCalls: () -> Unit = {},
    onStatistics: () -> Unit = {},
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        SpeedDialContent(
            modifier = modifier,
            folderTitle = folderTitle,
            onFoldersClick = onFoldersClick,
            onAddMessage = onAddMessage,
            onAddFolder = onAddFolder,
            onUnhandledCalls = onUnhandledCalls,
            onStatistics = onStatistics,
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun SpeedDialContent(
    modifier: Modifier = Modifier,
    folderTitle: String? = null,
    onFoldersClick: (() -> Unit)? = null,
    onAddMessage: () -> Unit = {},
    onAddFolder: () -> Unit = {},
    onUnhandledCalls: () -> Unit = {},
    onStatistics: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 135f else 0f,
        animationSpec = tween(durationMillis = 250)
    )

    val actions = listOf(
        Triple(R.string.unhandled_calls, R.drawable.ic_missed_call, onUnhandledCalls),
        Triple(R.string.add_folder, R.drawable.ic_new_folder, onAddFolder),
        Triple(R.string.add_message, R.drawable.ic_new_message, onAddMessage),
        Triple(R.string.statistics, R.drawable.ic_statistics, onStatistics),
    )

    BoxWithConstraints(modifier = modifier) {
        // The "+" button's centre sits 18% of the way up the screen, in easy thumb reach
        val bottomOffset = maxOf(24.dp, maxHeight * 0.18f - 30.dp)

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f))
                    .noRippleClickable { expanded = false }
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = bottomOffset, end = 20.dp)
        ) {
            actions.forEachIndexed { index, (label, icon, action) ->
                // Items closest to the main FAB appear first
                val delay = (actions.size - 1 - index) * 40
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(tween(180, delayMillis = delay)) +
                        scaleIn(tween(220, delayMillis = delay), initialScale = 0.4f) +
                        slideInVertically(tween(220, delayMillis = delay)) { it / 2 },
                    exit = fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.4f) +
                        slideOutVertically(tween(120)) { it / 2 }
                ) {
                    SpeedDialItem(
                        modifier = Modifier.padding(end = 6.dp),
                        label = stringResource(label),
                        icon = painterResource(icon),
                        onClick = {
                            expanded = false
                            action()
                        }
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Layout is LTR here: the folders button goes first (left), "+" last (right-most)
                if (onFoldersClick != null) {
                    AnimatedVisibility(
                        visible = !expanded,
                        enter = fadeIn(tween(200)) + scaleIn(tween(200), initialScale = 0.8f),
                        exit = fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.8f)
                    ) {
                        FolderPill(
                            title = folderTitle ?: stringResource(R.string.choose_folder),
                            onClick = onFoldersClick
                        )
                    }
                }

                FloatingActionButton(
                    onClick = { expanded = !expanded },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(20.dp),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 10.dp
                    ),
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.expansion_button),
                        modifier = Modifier
                            .size(30.dp)
                            .rotate(rotation)
                    )
                }
            }
        }
    }
}

/** Folder button styled as a sibling of the main "+" FAB (same size & shape, tonal color). */
@Composable
private fun FolderPill(title: String, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(20.dp),
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp
        ),
        modifier = Modifier.size(60.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_folder),
            contentDescription = "${stringResource(R.string.folders)}: $title",
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun SpeedDialItem(
    label: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SmallFloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
        modifier = modifier.size(48.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun MultiFab(
    fabs: List<MiniFloatingAction>,
    iconCollapsed: Painter,
    iconExpanded: Painter = iconCollapsed
) {
    var state by remember { mutableStateOf(MultiFabState.COLLAPSED) }
    val itemsCount = fabs.size
    val scaleFab: Float by animateFloatAsState(
        if (state == MultiFabState.EXPANDED) 1.2f else 1f,
        animationSpec = tween(durationMillis = 250)
    )

    val rotate: Float by animateFloatAsState(
        if (state == MultiFabState.EXPANDED) 180f else 0f,
        animationSpec = tween(durationMillis = 250)
    )

    val icon = if (state == MultiFabState.EXPANDED) iconExpanded else iconCollapsed

    val alpha: Float by animateFloatAsState(
        if (state == MultiFabState.EXPANDED) 1f else 0f,
        animationSpec = tween(durationMillis = 250)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        fabs.forEachIndexed { index, it ->
            MiniFloatingActionButton(
                modifier = Modifier
                    .graphicsLayer(
                        alpha = alpha,
                        scaleX = getScaleMini(state, itemsCount - index),
                        scaleY = getScaleMini(state, itemsCount - index),
                    ),
                miniFloatingAction = it,
                onClick = {
                    state =
                        if (state == MultiFabState.COLLAPSED) MultiFabState.EXPANDED else MultiFabState.COLLAPSED
                }
            )
        }
        FloatingActionButton(
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scaleFab,
                    scaleY = scaleFab,
                    rotationZ = rotate
                ),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(50),
            onClick = {
                state =
                    if (state == MultiFabState.COLLAPSED) MultiFabState.EXPANDED else MultiFabState.COLLAPSED
            }) {
            Icon(
                painter = icon,
                contentDescription = "Main Fab",
            )
        }
    }
}

@Composable
private fun getScaleMini(state: MultiFabState, position: Int): Float {
    val scaleMini: Float by animateFloatAsState(
        if (state == MultiFabState.EXPANDED) 1f else 0f,
        animationSpec = tween(durationMillis = 150 + 120 * position)
    )
    return scaleMini
}

enum class MultiFabState {
    COLLAPSED, EXPANDED
}
