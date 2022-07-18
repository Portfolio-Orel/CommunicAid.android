package com.orelzman.mymessages.presentation.components.multi_fab

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun MiniFloatingActionButton(
    miniFloatingAction: MiniFloatingAction,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { }
) {
    FloatingActionButton(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        onClick = {
            miniFloatingAction.action()
            onClick()
        }) {
        Icon(
            painter = miniFloatingAction.icon,
            contentDescription = miniFloatingAction.description
        )
    }
}

data class MiniFloatingAction(
    val action: () -> Unit,
    val icon: Painter,
    val description: String = "",
)