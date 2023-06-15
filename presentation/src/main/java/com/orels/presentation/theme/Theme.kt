package com.orels.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.orels.presentation.R

private val fontsVarelaround = FontFamily(
    Font(R.font.varelaround)
)

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
)


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
)

@Composable
fun MyMessagesTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (useDarkTheme) {
        DarkColors
    } else {
        LightColors
    }

    val tempType = Typography()

    MaterialTheme(
        colorScheme = colors,
        typography = Typography().copy(
            displayLarge = tempType.displayLarge.copy(fontFamily = fontsVarelaround),
            displayMedium = tempType.displayMedium.copy(fontFamily = fontsVarelaround),
            displaySmall = tempType.displaySmall.copy(fontFamily = fontsVarelaround),

            headlineLarge = tempType.headlineLarge.copy(fontFamily = fontsVarelaround),
            headlineMedium = tempType.headlineMedium.copy(fontFamily = fontsVarelaround),
            headlineSmall = tempType.headlineSmall.copy(fontFamily = fontsVarelaround),

            titleLarge = tempType.titleLarge.copy(fontFamily = fontsVarelaround),
            titleMedium = tempType.titleMedium.copy(fontFamily = fontsVarelaround),
            titleSmall = tempType.titleSmall.copy(fontFamily = fontsVarelaround),

            bodyLarge = tempType.bodyLarge.copy(fontFamily = fontsVarelaround),
            bodyMedium = tempType.bodyMedium.copy(fontFamily = fontsVarelaround),
            bodySmall = tempType.bodySmall.copy(fontFamily = fontsVarelaround),

            labelLarge = tempType.labelLarge.copy(fontFamily = fontsVarelaround),
            labelMedium = tempType.labelMedium.copy(fontFamily = fontsVarelaround),
            labelSmall = tempType.labelSmall.copy(fontFamily = fontsVarelaround)
        ),
        content = content
    )
}