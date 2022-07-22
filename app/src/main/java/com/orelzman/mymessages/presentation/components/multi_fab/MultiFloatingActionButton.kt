package com.orelzman.mymessages.presentation.components.multi_fab

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

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

//    val color: Color by animateColorAsState(
//        if (state == MultiFabState.EXPANDED) MaterialTheme.colorScheme.secondaryContainer
//        else MaterialTheme.colorScheme.secondary,
//        animationSpec = tween(durationMillis = 250)
//    )
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

