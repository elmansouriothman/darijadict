package com.example.darijadict.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF4B91F1),
    secondary = Color(0xFFEE964B),
    background = Color(0xFFF8F9FA),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
)

@Composable
fun DarijaTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColors

    // In your theme file (Theme.kt)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
