package com.orelzman.mymessages.presentation.components.charts.bar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orelzman.mymessages.domain.model.BarItem
import com.orelzman.mymessages.presentation.components.charts.side_values.SideValues

@Composable
fun BarChart(
    items: List<BarItem>,
) {
    if(items.isEmpty()) return
    val viewModel = BarViewModel(items)
    var selectedBar by remember { mutableStateOf<BarItem?>(null) }
    val maxValue = remember(items) {
        items.maxOfOrNull { it.value }?.toDouble() ?: 100.0
    }
    Row(
        modifier = Modifier
            .height(160.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        SideValues(
            maxValue = maxValue,
            minValue = 0.0,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .height(160.dp)
                .padding(8.dp)
        )
        items.sortedBy { it.value }.forEach {
            Bar(
                Modifier
                    .clickable {
                        selectedBar = if (selectedBar == it) null
                        else it
                    }
                    .padding(horizontal = 2.dp),
                barItem = it,
                normalizedValue = viewModel.getNormalizedValue(it),
                width = 38f,
                maxHeight = 100f,
                isSelected = it == selectedBar
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Bar(
    modifier: Modifier = Modifier,
    barItem: BarItem,
    normalizedValue: Float,
    width: Float,
    maxHeight: Float,
    isSelected: Boolean = false
) {
    val animatedFloat = remember { Animatable(0f) }
    LaunchedEffect(animatedFloat) {
        animatedFloat.animateTo(
            targetValue = normalizedValue * maxHeight,
            animationSpec = tween(durationMillis = 800, easing = LinearEasing)
        )
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = if (isSelected) barItem.value.toString() else "",
            style = MaterialTheme.typography.labelSmall,
            color = barItem.color
        )
        Card(
            modifier = Modifier
                .height(animatedFloat.value.dp)
                .width(width.dp),
            colors = CardDefaults.cardColors(
                containerColor = barItem.color
            ),
            shape = RoundedCornerShape(
                topStart = 30.dp,
                topEnd = 30.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            )
        ) {}
        Text(
            text = barItem.title,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall,
            color = barItem.color
        )
    }
}

@Preview
@Composable
fun BarPreview() = BarChart(
    listOf(
        BarItem("1", 23f, Color.Red),
        BarItem("2", 22f, Color.Blue),
        BarItem("3", 21f, Color.Red),
        BarItem("4", 20f, Color.Blue),
        BarItem("5", 65f, Color.Red),
        BarItem("6", 89f, Color.Blue),
    )
)