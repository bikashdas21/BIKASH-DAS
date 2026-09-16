package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Color(0xFF001F29),
    primaryContainer = Color(0xFF004D61),
    onPrimaryContainer = Color(0xFFBBE9FF),
    secondary = MeshGreen,
    onSecondary = Color(0xFF00220F),
    secondaryContainer = Color(0xFF005328),
    onSecondaryContainer = Color(0xFF75FF9D),
    tertiary = RelayGold,
    onTertiary = Color(0xFF261D00),
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkBorder,
    error = EmergencyRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCEF5FF),
    onPrimaryContainer = Color(0xFF001F28),
    secondary = Color(0xFF008947),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB9F7CD),
    onSecondaryContainer = Color(0xFF00210E),
    tertiary = Color(0xFF8B6C00),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder,
    error = EmergencyRed,
    onError = Color.White
)

@Composable
fun ZeroGridTheme(
    darkTheme: Boolean = true, // Bengali cybersecurity & mesh theme prefers dark default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
