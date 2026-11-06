package com.example.eyetracker_app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EyeTrackerDarkColorScheme = darkColorScheme(
    primary = AppAccent,
    onPrimary = Color(0xFF06231E),
    secondary = AppAccentDark,
    background = AppBackground,
    onBackground = AppText,
    surface = AppCard,
    onSurface = AppText,
    surfaceVariant = AppCard2,
    onSurfaceVariant = AppMuted,
    outline = AppBorder
)

@Composable
fun EyeTrackerAppTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EyeTrackerDarkColorScheme,
        typography = EyeTrackerTypography,
        content = content
    )
}
