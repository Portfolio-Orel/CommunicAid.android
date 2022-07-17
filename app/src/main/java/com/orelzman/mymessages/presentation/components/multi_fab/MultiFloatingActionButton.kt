package com.orelzman.mymessages.presentation.components.multi_fab

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun MultiFab(
    fabs: List<MiniFloatingAction>,
    fabIcon: ImageVector
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

    val color: Color by animateColorAsState(
        if (state == MultiFabState.EXPANDED) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(durationMillis = 250)
    )
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
            )
        }
        FloatingActionButton(
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scaleFab,
                    scaleY = scaleFab
                ),
            containerColor = color,
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

