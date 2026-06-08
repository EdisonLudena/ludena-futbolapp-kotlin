package com.futbolapp.theme

import androidx.compose.ui.graphics.Color

// ── Fondos (capas tonales, de más oscuro a más claro) ────────────────
val Background   = Color(0xFF080D0E)   // dp0 – fondo de pantalla
val Surface      = Color(0xFF0E1517)   // dp1 – scaffold, listas
val Surface2     = Color(0xFF141C1E)   // dp2 – tarjetas primarias
val Surface3     = Color(0xFF1A2325)   // dp3 – tarjetas anidadas, chips
val Border       = Color(0xFF1F2E31)   // borde estándar
val BorderLight  = Color(0xFF162024)   // borde sutil / separadores

// ── Textos ────────────────────────────────────────────────────────────
val TextPrimary   = Color(0xFFEEF5F6)  // títulos y contenido principal
val TextSecondary = Color(0xFF7DA8AE)  // subtítulos, metadatos
val TextFaint     = Color(0xFF3A5659)  // placeholders, labels terciarios

// ── Acento principal (Cyan Teal eléctrico) ────────────────────────────
val Accent       = Color(0xFF00C2D1)
val AccentLight  = Color(0xFF4DD9E3)
val AccentDark   = Color(0xFF008A96)
val AccentOnDark = Color(0xFF040A0B)   // texto sobre botón primario

// ── Estados semánticos ────────────────────────────────────────────────
val Success = Color(0xFF22D3A3)        // verde esmeralda – más sofisticado
val Warning = Color(0xFFF59E0B)
val Error   = Color(0xFFF2605C)        // rojo coral – menos agresivo
val Info    = Color(0xFF60A5FA)

// ── Estados de partido ────────────────────────────────────────────────
val StatusProgramado = Color(0xFF60A5FA)   // Próximo – azul
val StatusEnCurso    = Color(0xFF22D3A3)   // En juego – verde esmeralda
val StatusFinalizado = Color(0xFFA78BFA)   // Terminado – lavanda
val StatusCancelado  = Color(0xFFF2605C)   // Cancelado – coral

// ── Tarjetas de disciplina ────────────────────────────────────────────
val TarjetaAmarilla = Color(0xFFF59E0B)
val TarjetaRoja     = Color(0xFFF2605C)