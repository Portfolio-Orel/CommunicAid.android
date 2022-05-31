package com.orelzman.mymessages.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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
    tertiary = SkyBlue80,
    onTertiary = SkyBlue20,
    tertiaryContainer = SkyBlue30,
    onTertiaryContainer = SkyBlue90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
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
    primary = Purple40,
    onPrimary = Color.White,
    primaryContainer = Purple90,
    onPrimaryContainer = Purple10,
    inversePrimary = Purple80,
    secondary = DarkPurple40,
    onSecondary = Color.White,
    secondaryContainer = DarkPurple90,
    onSecondaryContainer = DarkPurple10,
    tertiary = SkyBlue40,
    onTertiary = Color.White,
    tertiaryContainer = SkyBlue90,
    onTertiaryContainer = SkyBlue10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
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
    val useDynamicColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colors = when {
        useDynamicColors && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        useDynamicColors && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
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