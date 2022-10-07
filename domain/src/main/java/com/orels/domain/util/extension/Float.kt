package com.orels.domain.util.extension

fun Float.normalizeBetween0AndMax(minValue: Float, maxValue: Float, max: Float = 1f) =
    (this - minValue)/(maxValue - minValue) * max