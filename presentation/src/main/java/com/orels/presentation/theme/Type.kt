package com.orels.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.orels.presentation.R

// Set of Material typography styles to start with

val fontsVarelaround = FontFamily(
    Font(R.font.varelaround)
)

val Typography = Typography(
    displaySmall = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    displayMedium = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    displayLarge = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    headlineSmall = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    headlineMedium = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    headlineLarge = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 38.sp
    ),
    bodySmall = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    bodyMedium = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    titleLarge = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 38.sp
    ),
    labelSmall = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelMedium = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    labelLarge = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)