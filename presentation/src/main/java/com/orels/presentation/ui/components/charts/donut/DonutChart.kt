package com.orelzman.mymessages.presentation.components.charts.donut

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orelzman.mymessages.presentation.components.charts.model.DonutItem

@Composable
fun DonutChart(
    item: DonutItem
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DonutCircles(item)
        Column(content = item.title)
    }
}

@Composable
private fun DonutCircles(item: DonutItem) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .innerCircle(
                    radius = item.innerSize.dp,
                    color = item.innerColor
                ),
            contentAlignment = Alignment.Center,
            content = item.textInside
        )
        AnimatedOuterCircle(size = item.outerSize.dp, color = item.outerColor)
    }
}

@Composable
private fun AnimatedOuterCircle(size: Dp, color: Color) {
    val animatedFloat = remember { Animatable(0f) }

    LaunchedEffect(animatedFloat) {
        animatedFloat.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = LinearEasing)
        )
    }

    Canvas(
        modifier = Modifier
            .padding(12.dp)
            .size(size)
    ) {
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 360f * animatedFloat.value,
            useCenter = false,
            style = Stroke(width = 6f, cap = StrokeCap.Round),
        )
    }
}

//@Composable
//fun DonutCircles(item: DonutItem) {
//    val animatedFloat = remember { Animatable(0f) }
//
//    LaunchedEffect(animatedFloat) {
//        animatedFloat.animateTo(
//            targetValue = 1f,
//            animationSpec = tween(durationMillis = 1000, easing = LinearEasing))
//    }
//
//    Canvas(
//        modifier = Modifier
//            .padding(12.dp)
//            .size(100.dp)
//    ) {
//        drawArc(
//            color = item.outerColor,
//            startAngle = 0f,
//            sweepAngle = 360f * animatedFloat.value,
//            useCenter = false,
//            style = Stroke(width = 25f, cap = StrokeCap.Round),
//        )
//
//        drawCircle(
//            color = item.innerColor,
//            radius = item.innerRadius,
//        )
//        drawContext.canvas.nativeCanvas.apply {
//            drawText(
//                "Hey, Himanshu",
//                size.width / 2,
//                size.height / 2,
//                Paint().apply {
//                    textSize = 100f
//                    color = android.graphics.Color.BLUE
//                    textAlign = Paint.Align.CENTER
//                }
//            )
//        }
//    }
//}

private fun Modifier.outerCircle(radius: Dp = 100.dp, color: Color = Color.Red): Modifier = this
    .clip(CircleShape)
    .size(radius)
    .background(color)

private fun Modifier.innerCircle(radius: Dp = 80.dp, color: Color = Color.White): Modifier = this
    .clip(CircleShape)
    .size(radius)
    .background(color)

@Preview
@Composable
fun DonutPreview() = DonutChart(
    DonutItem(
        title = { Text(text = "שיחות יוצאות") },
        textInside = { Text(text = "14") },
    )
)