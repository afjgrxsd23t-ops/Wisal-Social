package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = WisalRoyalBlue,
    onPrimary = Color.White,
    primaryContainer = WisalCardNavy,
    onPrimaryContainer = WisalCyanGlow,
    secondary = WisalCyanGlow,
    onSecondary = WisalMidnightNavy,
    background = WisalBgDark,
    onBackground = WisalTextPrimaryDark,
    surface = WisalCardDark,
    onSurface = WisalTextPrimaryDark,
    surfaceVariant = WisalCardNavy,
    onSurfaceVariant = WisalTextSecondaryDark,
    outline = WisalBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = WisalRoyalBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8EFFF),
    onPrimaryContainer = WisalRoyalBlueDark,
    secondary = WisalRoyalBlueDark,
    onSecondary = Color.White,
    background = WisalBgLight,
    onBackground = WisalTextPrimaryLight,
    surface = WisalCardLight,
    onSurface = WisalTextPrimaryLight,
    surfaceVariant = Color(0xFFEDF2F9),
    onSurfaceVariant = WisalTextSecondaryLight,
    outline = WisalBorderLight
)

@Composable
fun WisalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WisalTypography,
        content = content
    )
}
