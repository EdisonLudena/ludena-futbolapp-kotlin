package com.futbolapp.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    // ── Displays (marcadores, nombres de liga) ──────────────────────
    displayLarge = TextStyle(
        fontWeight    = FontWeight.ExtraBold,   // W800
        fontSize      = 36.sp,
        lineHeight    = 42.sp,
        letterSpacing = (-1.0).sp,
    ),
    displayMedium = TextStyle(
        fontWeight    = FontWeight.Bold,         // W700
        fontSize      = 28.sp,
        lineHeight    = 34.sp,
        letterSpacing = (-0.5).sp,
    ),
    displaySmall = TextStyle(
        fontWeight    = FontWeight.Bold,
        fontSize      = 22.sp,
        lineHeight    = 28.sp,
        letterSpacing = (-0.25).sp,
    ),

    // ── Headlines (sección de pantalla) ─────────────────────────────
    headlineLarge = TextStyle(
        fontWeight    = FontWeight.SemiBold,     // W600
        fontSize      = 22.sp,
        lineHeight    = 30.sp,
        letterSpacing = (-0.2).sp,
    ),
    headlineMedium = TextStyle(
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 18.sp,
        lineHeight    = 26.sp,
    ),
    headlineSmall = TextStyle(
        fontWeight    = FontWeight.Medium,       // W500
        fontSize      = 16.sp,
        lineHeight    = 24.sp,
    ),

    // ── Titles (nombres de tarjeta, botones de texto) ────────────────
    titleLarge = TextStyle(
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 17.sp,
        lineHeight    = 24.sp,
    ),
    titleMedium = TextStyle(
        fontWeight    = FontWeight.Medium,
        fontSize      = 15.sp,
        lineHeight    = 22.sp,
        letterSpacing = 0.1.sp,
    ),
    titleSmall = TextStyle(
        fontWeight    = FontWeight.Medium,
        fontSize      = 13.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.1.sp,
    ),

    // ── Body (contenido largo, descripciones) ────────────────────────
    bodyLarge = TextStyle(
        fontWeight    = FontWeight.Normal,       // W400
        fontSize      = 15.sp,
        lineHeight    = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight    = FontWeight.Normal,
        fontSize      = 13.sp,
        lineHeight    = 20.sp,
    ),
    bodySmall = TextStyle(
        fontWeight    = FontWeight.Normal,
        fontSize      = 12.sp,
        lineHeight    = 18.sp,
        letterSpacing = 0.1.sp,
    ),

    // ── Labels (badges, timestamps, stats) ───────────────────────────
    labelLarge = TextStyle(
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 13.sp,
        letterSpacing = 0.4.sp,
    ),
    labelMedium = TextStyle(
        fontWeight    = FontWeight.Medium,
        fontSize      = 11.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontWeight    = FontWeight.Bold,         // W700
        fontSize      = 10.sp,
        letterSpacing = 0.8.sp,
    ),
)