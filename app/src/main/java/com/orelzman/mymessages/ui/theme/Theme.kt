package com.orelzman.mymessages.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = Purple80,
    onPrimary = Purple20,
    primaryContainer = Purple30,
    onPrimaryContainer = Purple90,
    inversePrimary = Purple40,
    secondary = DarkPurple80,
    onSecondary = DarkPurple20,
    secondaryContainer = DarkPurple30,
    onSecondaryContainer = DarkPurple90,
    tertiary = Green80,
    onTertiary = Green20,
    tertiaryContainer = Green30,
    onTertiaryContainer = Green90,
    error = Red80,
    onError = Red20,
    errorContainer = Red40,
    onErrorContainer = Red90,
    background = Grey10,
    onBackground = Grey90,
    surface = PurpleGrey30,
    onSurface = PurpleGrey80,
    inverseSurface = Grey90,
    inverseOnSurface = Grey10,
    surfaceVariant = PurpleGrey30,
    onSurfaceVariant = PurpleGrey80,
    outline = PurpleGrey80
)

private val LightColorPalette = lightColorScheme(
    primary = Purple80,
    onPrimary = Purple20,
    primaryContainer = Purple90,
    onPrimaryContainer = Purple10,
    inversePrimary = Purple20,
    secondary = DarkPurple40,
    onSecondary = Color.White,
    secondaryContainer = DarkPurple90,
    onSecondaryContainer = DarkPurple10,
    tertiary = Green40,
    onTertiary = Color.White,
    tertiaryContainer = Green90,
    onTertiaryContainer = Green10,
    error = Red80,
    onError = Red20,
    errorContainer = Red40,
    onErrorContainer = Red90,
    background = Grey99,
    onBackground = Grey10,
    surface = PurpleGrey90,
    onSurface = PurpleGrey30,
    inverseSurface = Grey20,
    inverseOnSurface = Grey95,
    surfaceVariant = PurpleGrey90,
    onSurfaceVariant = PurpleGrey30,
    outline = PurpleGrey50
)

@Composable
fun MyMessagesTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
//    val useDynamicColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colors = when {
//        useDynamicColors && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
//        useDynamicColors && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}