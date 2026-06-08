package com.futbolapp.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),    // chips, badges, pills pequeñas
    small      = RoundedCornerShape(10.dp),   // campos de texto, snackbars
    medium     = RoundedCornerShape(14.dp),   // bottom sheets, diálogos
    large      = RoundedCornerShape(18.dp),   // tarjetas principales (MatchCard, PlayerCard)
    extraLarge = RoundedCornerShape(24.dp),   // modales full-bottom, hero cards
)

// Formas semánticas nombradas (úsalas directamente en los Composables)
val ShapeCard        = RoundedCornerShape(18.dp)  // ElevatedCard, OutlinedCard
val ShapeButton      = RoundedCornerShape(12.dp)  // Button, FilledButton
val ShapeChip        = RoundedCornerShape(99.dp)  // status pills, FilterChip
val ShapeAvatar      = RoundedCornerShape(14.dp)  // avatares de jugador/equipo
val ShapeInput       = RoundedCornerShape(10.dp)  // TextField
val ShapeBottomSheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 0.dp, bottomEnd = 0.dp)