package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private fun buildTvColorScheme(palette: TvThemePalette): ColorScheme = darkColorScheme(
    primary = palette.electricCyan,
    onPrimary = palette.deepObsidian,
    primaryContainer = palette.cyanContainer,
    onPrimaryContainer = palette.electricCyan,
    secondary = palette.indigoGlow,
    onSecondary = palette.textPrimary,
    secondaryContainer = palette.elevatedSlate,
    onSecondaryContainer = palette.textPrimary,
    tertiary = palette.amberGold,
    onTertiary = palette.deepObsidian,
    error = palette.broadcastCoral,
    onError = Color.White,
    background = palette.deepObsidian,
    onBackground = palette.textPrimary,
    surface = palette.midnightNavy,
    onSurface = palette.textPrimary,
    surfaceVariant = palette.slateSurface,
    onSurfaceVariant = palette.textSecondary,
    surfaceContainerLow = palette.midnightNavy,
    surfaceContainer = palette.slateSurface,
    surfaceContainerHigh = palette.elevatedSlate,
    outline = palette.glassBorder,
    outlineVariant = palette.glassBorder.copy(alpha = 0.5f)
)

val NovaTvShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(26.dp)
)

@Composable
fun NovaTvTheme(
    themeOption: AppThemeOption = AppThemeOption.CYBER_OBSIDIAN,
    content: @Composable () -> Unit
) {
    val palette = themeOption.palette
    CompositionLocalProvider(LocalTvThemePalette provides palette) {
        MaterialTheme(
            colorScheme = buildTvColorScheme(palette),
            typography = NovaTvTypography,
            shapes = NovaTvShapes,
            content = content
        )
    }
}
