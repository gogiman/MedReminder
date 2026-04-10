package com.example.medreminder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Blue80, secondary = BlueGrey80, tertiary = Teal80
)
private val LightColorScheme = lightColorScheme(
    primary = Blue40, secondary = BlueGrey40, tertiary = Teal40
)

@Composable
fun MedReminderTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
