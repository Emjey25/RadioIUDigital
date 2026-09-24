package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricPurple,
    onPrimary = Color.White,
    primaryContainer = MagentaPrimary,
    onPrimaryContainer = Color.White,
    secondary = NeonCyan,
    onSecondary = Color.Black,
    secondaryContainer = CardDarkElevated,
    onSecondaryContainer = Color.White,
    tertiary = VioletGlow,
    onTertiary = Color.White,
    background = PureBlack,
    onBackground = TextPrimary,
    surface = DarkMatteSurface,
    onSurface = TextPrimary,
    surfaceVariant = CardDarkSurface,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    outlineVariant = CardDarkHighlight,
    error = AlertRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
