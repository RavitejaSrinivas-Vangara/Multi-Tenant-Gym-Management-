package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = HighDensityPrimaryDark,
    secondary = SecondaryTeal,
    tertiary = AccentGreen,
    background = HighDensityBgDark,
    surface = HighDensityBgDark,
    error = ErrorRed,
    onPrimary = Color(0xFF381E72),
    onSecondary = Color.Black,
    onBackground = HighDensityTextDark,
    onSurface = HighDensityTextDark,
    onError = Color.White,
    primaryContainer = HighDensityContainerDark,
    onPrimaryContainer = HighDensityOnContainerDark,
    surfaceVariant = HighDensitySurfaceVariantDark,
    onSurfaceVariant = HighDensityOnContainerDark,
    outline = HighDensityOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = HighDensityPrimaryLight,
    secondary = SecondaryTeal,
    tertiary = AccentGreen,
    background = HighDensityBgLight,
    surface = Color.White,
    error = ErrorRed,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = HighDensityTextLight,
    onSurface = HighDensityTextLight,
    onError = Color.White,
    primaryContainer = HighDensityContainerLight,
    onPrimaryContainer = HighDensityOnContainerLight,
    surfaceVariant = HighDensitySurfaceVariantLight,
    onSurfaceVariant = HighDensityOnContainerLight,
    outline = HighDensityOutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
