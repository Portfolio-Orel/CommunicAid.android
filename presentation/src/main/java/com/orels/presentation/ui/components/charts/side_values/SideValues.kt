package com.orels.presentation.ui.components.charts.side_values

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun SideValues(
    maxValue: Double,
    minValue: Double,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val spacing = 100f
    val density = LocalDensity.current
    val elementsCount = 5f
    val textPaint = remember(density) {
        Paint().apply {
            this.color = color.toArgb()
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }
    }

    Canvas(modifier = modifier) {
        val step = (maxValue - minValue) / elementsCount
        (0..(elementsCount - 1).toInt()).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    (minValue + step * i).roundToInt().toString(),
                    30f,
                    size.height - spacing - i * size.height / elementsCount,
                    textPaint
                )
            }
        }
    }
}