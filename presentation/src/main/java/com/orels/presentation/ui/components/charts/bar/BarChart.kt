package com.orels.presentation.ui.components.charts.bar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.orels.presentation.ui.components.charts.model.BarItem

@Composable
fun BarChart(
    items: List<BarItem>,
) {
    if (items.isEmpty()) return
    val viewModel = BarViewModel(items)
    LazyRow(
        modifier = Modifier
            .height(200.dp)
            .padding(16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState(initial = 10)),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        items(items.sortedBy { it.value }) {
            Bar(
                Modifier
                    .padding(horizontal = 2.dp),
                barItem = it,
                normalizedValue = viewModel.getNormalizedValue(it),
                width = 28f,
                maxHeight = 100f
            )
        }
    }
}

@Composable
fun Bar(
    modifier: Modifier = Modifier,
    barItem: BarItem,
    normalizedValue: Float,
    width: Float,
    maxHeight: Float
) {
    val animatedFloat = remember { Animatable(0f) }
    val color = Brush.verticalGradient(
        0f to barItem.color,
        1f to barItem.color.copy(alpha = 0.45f)
    )

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
            text = barItem.value.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = barItem.color
        )
        Box(
            modifier = Modifier
                .height(animatedFloat.value.dp)
                .width(width.dp)
                .shadow(
                    elevation = 0.dp, shape = RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 10.dp,
                        bottomEnd = 4.dp,
                        bottomStart = 4.dp
                    ), clip = true
                )
                .background(color),
        )
        Text(
            text = barItem.title,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun BarPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        BarChart(
            listOf(
                BarItem("1", 23f, MaterialTheme.colorScheme.primary),
                BarItem("2", 22f, MaterialTheme.colorScheme.tertiary),
                BarItem("3", 21f, MaterialTheme.colorScheme.primary),
                BarItem("4", 20f, MaterialTheme.colorScheme.tertiary),
                BarItem("5", 65f, MaterialTheme.colorScheme.primary),
                BarItem("6", 89f, MaterialTheme.colorScheme.tertiary),
            )
        )
    }
}