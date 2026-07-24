package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PureBlack,
    onPrimary = PureWhite,
    primaryContainer = DarkGray,
    onPrimaryContainer = PureWhite,
    secondary = DarkGray,
    onSecondary = PureWhite,
    secondaryContainer = LightGray,
    onSecondaryContainer = PureBlack,
    tertiary = MediumGray,
    background = PureWhite,
    onBackground = PureBlack,
    surface = PureWhite,
    onSurface = PureBlack,
    surfaceVariant = LightGray,
    onSurfaceVariant = MediumGray,
    outline = BorderGray,
    error = DarkGray,
    onError = PureWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = PureBlack,
    primaryContainer = Color(0xFF333333),
    onPrimaryContainer = PureWhite,
    secondary = Color(0xFFCCCCCC),
    onSecondary = PureBlack,
    secondaryContainer = Color(0xFF222222),
    onSecondaryContainer = PureWhite,
    tertiary = Color(0xFFAAAAAA),
    background = Color(0xFF121212),
    onBackground = PureWhite,
    surface = Color(0xFF1E1E1E),
    onSurface = PureWhite,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFBBBBBB),
    outline = Color(0xFF444444),
    error = Color(0xFFCF6679),
    onError = PureBlack
)

@Composable
fun MlaebiTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


