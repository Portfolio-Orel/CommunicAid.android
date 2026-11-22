package com.orels.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.orels.presentation.R

val fontsVarelaround = FontFamily(
    Font(R.font.varelaround)
)

private val AppFont = fontsVarelaround

private fun style(size: Int, line: Int, weight: FontWeight = FontWeight.Normal, spacing: Float = 0f) =
    TextStyle(
        fontFamily = AppFont,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = line.sp,
        letterSpacing = spacing.sp,
    )

/**
 * Coherent Material 3 type scale on top of Varela Round (Hebrew friendly).
 * Sizes are kept slightly larger than the M3 defaults for readability.
 */
val Typography = Typography(
    displayLarge = style(48, 56, FontWeight.Bold),
    displayMedium = style(40, 48, FontWeight.Bold),
    displaySmall = style(34, 42, FontWeight.Bold),
    headlineLarge = style(32, 40, FontWeight.Bold),
    headlineMedium = style(28, 36, FontWeight.SemiBold),
    headlineSmall = style(24, 32, FontWeight.SemiBold),
    titleLarge = style(22, 28, FontWeight.SemiBold),
    titleMedium = style(18, 24, FontWeight.SemiBold),
    titleSmall = style(16, 22, FontWeight.Medium),
    bodyLarge = style(17, 24),
    bodyMedium = style(15, 22),
    bodySmall = style(13, 18),
    labelLarge = style(15, 20, FontWeight.Medium),
    labelMedium = style(13, 18, FontWeight.Medium),
    labelSmall = style(11, 16, FontWeight.Medium, 0.2f),
)
