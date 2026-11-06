package com.vicare.family.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FamilyColors = lightColorScheme(
    primary = Color(0xFF147FB5),
    onPrimary = Color.White,
    secondary = Color(0xFF168A78),
    background = Color(0xFFF4F8FB),
    surface = Color.White,
    onSurface = Color(0xFF20394A),
    error = Color(0xFFC84B5A),
)

@Composable
fun VicareFamilyTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = FamilyColors, content = content)
}
