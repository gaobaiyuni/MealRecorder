package com.mealrecorder.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Primary,
    secondary = DinnerColor,
    background = BackgroundLight,
    surface = CardLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun MealRecorderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
