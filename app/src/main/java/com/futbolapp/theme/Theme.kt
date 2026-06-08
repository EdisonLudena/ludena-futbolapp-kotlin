package com.futbolapp.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    // ── Primario (Accent = Cyan Teal) ─────────────────────────────────
    primary            = Accent,
    onPrimary          = AccentOnDark,
    primaryContainer   = Color(0xFF00363B),   // contenedor de chips activos
    onPrimaryContainer = AccentLight,

    // ── Secundario ────────────────────────────────────────────────────
    secondary            = AccentLight,
    onSecondary          = AccentOnDark,
    secondaryContainer   = Color(0xFF0A2325),
    onSecondaryContainer = AccentLight,

    // ── Terciario (lavanda – para estados finalizados, etc.) ──────────
    tertiary            = Color(0xFFA78BFA),
    onTertiary          = Color(0xFF0D0518),
    tertiaryContainer   = Color(0xFF1A0D35),
    onTertiaryContainer = Color(0xFFD4BBFF),

    // ── Fondos y superficies ──────────────────────────────────────────
    background       = Background,
    onBackground     = TextPrimary,
    surface          = Surface,
    onSurface        = TextPrimary,
    surfaceVariant   = Surface2,
    onSurfaceVariant = TextSecondary,

    // ── Contenedores elevados (usa tonalElevation para variar entre Surface, Surface2, Surface3) ──
    surfaceTint      = Accent,          // Material3 usa este color para la tonal elevation

    // ── Bordes ────────────────────────────────────────────────────────
    outline        = Border,
    outlineVariant = BorderLight,

    // ── Error ─────────────────────────────────────────────────────────
    error        = Error,
    onError      = Color(0xFF2A0000),
    errorContainer   = Color(0xFF3D0F0F),
    onErrorContainer = Color(0xFFFFA8A6),

    // ── Inverse (para snackbars / tooltips claros) ────────────────────
    inverseSurface   = Color(0xFFDEE8EA),
    inverseOnSurface = Color(0xFF0E1517),
    inversePrimary   = AccentDark,

    scrim = Color(0x80000000),
)

@Composable
fun FutbolAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = Typography,
        shapes      = Shapes,
        content     = content,
    )
}