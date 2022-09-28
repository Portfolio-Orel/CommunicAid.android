package com.orelzman.mymessages.presentation.components.charts.domain.extension

fun Float.normalizeBetween0AndMax(minValue: Float, maxValue: Float, max: Float = 1f) =
    (this - minValue)/(maxValue - minValue) * max