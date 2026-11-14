package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val NovaTvColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = DeepObsidian,
    primaryContainer = CyanContainer,
    onPrimaryContainer = ElectricCyan,
    secondary = IndigoGlow,
    onSecondary = TextPrimary,
    secondaryContainer = ElevatedSlate,
    onSecondaryContainer = TextPrimary,
    tertiary = AmberGold,
    onTertiary = DeepObsidian,
    error = BroadcastCoral,
    onError = Color.White,
    background = DeepObsidian,
    onBackground = TextPrimary,
    surface = MidnightNavy,
    onSurface = TextPrimary,
    surfaceVariant = SlateSurface,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLow = MidnightNavy,
    surfaceContainer = SlateSurface,
    surfaceContainerHigh = ElevatedSlate,
    outline = GlassBorder,
    outlineVariant = GlassBorder.copy(alpha = 0.5f)
)

val NovaTvShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(26.dp)
)

@Composable
fun NovaTvTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NovaTvColorScheme,
        typography = NovaTvTypography,
        shapes = NovaTvShapes,
        content = content
    )
}
