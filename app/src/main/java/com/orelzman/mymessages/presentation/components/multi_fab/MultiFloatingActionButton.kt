package com.orelzman.mymessages.presentation.components.multi_fab

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun MultiFab(
    fabs: List<MiniFloatingAction>,
    fabIcon: ImageVector,
) {
    var state by remember { mutableStateOf(MultiFabState.COLLAPSED) }

    val scaleMini: Float by animateFloatAsState(
        if (state == MultiFabState.EXPANDED) 1f else 0f,
        animationSpec = tween(durationMillis = 250)
    )

    val scaleFab: Float by animateFloatAsState(
        if (state == MultiFabState.EXPANDED) 1.15f else 1f,
        animationSpec = tween(durationMillis = 250)
    )

    val alpha: Float by animateFloatAsState(
        if (state == MultiFabState.EXPANDED) 1f else 0f,
        animationSpec = tween(durationMillis = 250)
    )

//    val color: Color by animateColorAsState(
//        if (state == MultiFabState.EXPANDED) MaterialTheme.colorScheme.secondaryContainer
//        else MaterialTheme.colorScheme.secondary,
//        animationSpec = tween(durationMillis = 250)
//    )
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        fabs.forEach {
            MiniFloatingActionButton(
                modifier = Modifier
                    .graphicsLayer(
                        alpha = alpha,
                        scaleX = scaleMini,
                        scaleY = scaleMini,
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
                    scaleY = scaleFab
                ),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(50),
            onClick = {
                state =
                    if (state == MultiFabState.COLLAPSED) MultiFabState.EXPANDED else MultiFabState.COLLAPSED
            }) {
            Icon(
                imageVector = fabIcon,
                contentDescription = "Main Fab",
            )
        }
    }
}


enum class MultiFabState {
    COLLAPSED, EXPANDED
}

