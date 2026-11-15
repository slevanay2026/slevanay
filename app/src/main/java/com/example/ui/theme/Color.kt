package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class TvThemePalette(
    val deepObsidian: Color,
    val midnightNavy: Color,
    val slateSurface: Color,
    val elevatedSlate: Color,
    val glassBorder: Color,
    val electricCyan: Color,
    val cyanContainer: Color,
    val indigoGlow: Color,
    val broadcastCoral: Color = Color(0xFFFF5252),
    val amberGold: Color = Color(0xFFFFB300),
    val emeraldLive: Color = Color(0xFF10B981),
    val textPrimary: Color = Color(0xFFF8FAFC),
    val textSecondary: Color = Color(0xFF94A3B8),
    val textMuted: Color = Color(0xFF64748B)
)

enum class AppThemeOption(
    val id: String,
    val palette: TvThemePalette
) {
    CYBER_OBSIDIAN(
        id = "cyber_obsidian",
        palette = TvThemePalette(
            deepObsidian = Color(0xFF070B12),
            midnightNavy = Color(0xFF0D1526),
            slateSurface = Color(0xFF131C2E),
            elevatedSlate = Color(0xFF1A263D),
            glassBorder = Color(0xFF283754),
            electricCyan = Color(0xFF00E5FF),
            cyanContainer = Color(0xFF003B47),
            indigoGlow = Color(0xFF6366F1)
        )
    ),
    ROYAL_AMETHYST(
        id = "royal_amethyst",
        palette = TvThemePalette(
            deepObsidian = Color(0xFF090612),
            midnightNavy = Color(0xFF130D24),
            slateSurface = Color(0xFF1B1333),
            elevatedSlate = Color(0xFF251B45),
            glassBorder = Color(0xFF3D2E6B),
            electricCyan = Color(0xFFC084FC),
            cyanContainer = Color(0xFF3B1A5E),
            indigoGlow = Color(0xFFF472B6)
        )
    ),
    EMERALD_STADIUM(
        id = "emerald_stadium",
        palette = TvThemePalette(
            deepObsidian = Color(0xFF050E0A),
            midnightNavy = Color(0xFF0A1B14),
            slateSurface = Color(0xFF10271D),
            elevatedSlate = Color(0xFF173628),
            glassBorder = Color(0xFF26543F),
            electricCyan = Color(0xFF10B981),
            cyanContainer = Color(0xFF064E3B),
            indigoGlow = Color(0xFF34D399)
        )
    ),
    CRIMSON_CINEMA(
        id = "crimson_cinema",
        palette = TvThemePalette(
            deepObsidian = Color(0xFF100608),
            midnightNavy = Color(0xFF1C0C10),
            slateSurface = Color(0xFF271117),
            elevatedSlate = Color(0xFF351820),
            glassBorder = Color(0xFF542834),
            electricCyan = Color(0xFFFF5A6E),
            cyanContainer = Color(0xFF5B1222),
            indigoGlow = Color(0xFFFFB300)
        )
    ),
    GOLDEN_ONYX(
        id = "golden_onyx",
        palette = TvThemePalette(
            deepObsidian = Color(0xFF0C0905),
            midnightNavy = Color(0xFF18120A),
            slateSurface = Color(0xFF221A0E),
            elevatedSlate = Color(0xFF2F2313),
            glassBorder = Color(0xFF4A381F),
            electricCyan = Color(0xFFFFB300),
            cyanContainer = Color(0xFF4D3500),
            indigoGlow = Color(0xFFF59E0B)
        )
    ),
    OCEAN_COBALT(
        id = "ocean_cobalt",
        palette = TvThemePalette(
            deepObsidian = Color(0xFF040B17),
            midnightNavy = Color(0xFF0A172E),
            slateSurface = Color(0xFF10213F),
            elevatedSlate = Color(0xFF172E54),
            glassBorder = Color(0xFF26477D),
            electricCyan = Color(0xFF38BDF8),
            cyanContainer = Color(0xFF0C4A6E),
            indigoGlow = Color(0xFF818CF8)
        )
    );

    companion object {
        fun fromId(id: String?): AppThemeOption =
            entries.firstOrNull { it.id == id } ?: CYBER_OBSIDIAN
    }
}

val LocalTvThemePalette = staticCompositionLocalOf { AppThemeOption.CYBER_OBSIDIAN.palette }

// Dynamic theme-aware tokens resolved from LocalTvThemePalette
val DeepObsidian: Color
    @Composable @ReadOnlyComposable get() = LocalTvThemePalette.current.deepObsidian
val MidnightNavy: Color
    @Composable @ReadOnlyComposable get() = LocalTvThemePalette.current.midnightNavy
val SlateSurface: Color
    @Composable @ReadOnlyComposable get() = LocalTvThemePalette.current.slateSurface
val ElevatedSlate: Color
    @Composable @ReadOnlyComposable get() = LocalTvThemePalette.current.elevatedSlate
val GlassBorder: Color
    @Composable @ReadOnlyComposable get() = LocalTvThemePalette.current.glassBorder

val ElectricCyan: Color
    @Composable @ReadOnlyComposable get() = LocalTvThemePalette.current.electricCyan
val CyanContainer: Color
    @Composable @ReadOnlyComposable get() = LocalTvThemePalette.current.cyanContainer
val IndigoGlow: Color
    @Composable @ReadOnlyComposable get() = LocalTvThemePalette.current.indigoGlow

val BroadcastCoral = Color(0xFFFF5252)
val AmberGold = Color(0xFFFFB300)
val EmeraldLive = Color(0xFF10B981)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)
