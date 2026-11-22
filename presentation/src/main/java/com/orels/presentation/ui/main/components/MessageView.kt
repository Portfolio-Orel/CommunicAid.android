package com.orels.presentation.ui.main.components

import android.content.Context
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orels.domain.model.entities.Message
import com.orels.presentation.theme.AppTheme
import com.orels.presentation.theme.MyMessagesTheme

private val TileShape = RoundedCornerShape(22.dp)

@ExperimentalFoundationApi
@Composable
fun MessageView(
    message: Message,
    modifier: Modifier = Modifier,
    borderColor: Color = AppTheme.extended.cardBorder,
    onClick: (Message) -> Unit,
    onLongClick: (Message, Context) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(0.76f)
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .shadow(
                    elevation = 3.dp,
                    shape = TileShape,
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                )
                .clip(TileShape)
                .background(AppTheme.extended.cardSurface)
                .border(width = 1.dp, color = borderColor, shape = TileShape)
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = androidx.compose.material.ripple.rememberRipple(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    onClick = { onClick(message) },
                    onLongClick = { onLongClick(message, context) }
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = message.shortTitle,
                fontSize = 34.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
        }
        Text(
            modifier = Modifier
                .weight(0.24f)
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            text = message.title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@ExperimentalFoundationApi
@Preview(showBackground = true, backgroundColor = 0xFFF7F6FB)
@Composable
fun DefaultPreview() {
    MyMessagesTheme {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MessageView(
                message = Message.default,
                modifier = Modifier
                    .height(120.dp)
                    .width(80.dp),
                onClick = { _ -> }
            )
        }
    }
}
