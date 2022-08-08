package com.orelzman.mymessages.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.orelzman.mymessages.R

// Set of Material typography styles to start with

val fontsVarelaround = FontFamily(
    Font(R.font.varelaround)
)

val Typography = Typography(
    bodySmall = TextStyle(
        fontFamily = fontsVarelaround,
        fontWeight = FontWeight.Thin,
        fontSize = 12.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = fontsVarelaround,
        fontWeight = FontWeight.Thin,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(
        fontFamily = fontsVarelaround,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = fontsVarelaround,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = fontsVarelaround,
        fontWeight = FontWeight.Normal,
        fontSize = 38.sp
    ),
    labelSmall = TextStyle(
        fontFamily = fontsVarelaround,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelMedium = TextStyle(
        fontFamily = fontsVarelaround,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = fontsVarelaround,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
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