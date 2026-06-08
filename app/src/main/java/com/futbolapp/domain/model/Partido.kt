package com.futbolapp.domain.model

data class Partido(
    val id: Int,
    val rival: String,
    val fecha: String,          // ISO 8601: "2026-06-15T18:00:00Z"
    val lugar: String,
    val resultadoFinal: String?, // null mientras no haya terminado
    val isActive: Boolean,
)

data class PartidoPayload(
    val rival: String,
    val fecha: String,
    val lugar: String,
    val isActive: Boolean,
)

data class TemporadaStats(
    val totalPartidos: Int,
    val victorias: Int,
    val derrotas: Int,
    val empates: Int,
    val golesFavor: Int,
    val golesContra: Int,
)