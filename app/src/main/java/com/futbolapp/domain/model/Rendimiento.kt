package com.futbolapp.domain.model

data class EventoRendimiento(
    val id: Int,
    val jugadorId: Int,
    val partidoId: Int,
    val minutosJugados: Int,
    val goles: Int,
    val asistencias: Int,
    val tarjetasAmarillas: Int,
    val tarjetaRoja: Boolean,
)

data class RendimientoPayload(
    val jugadorId: Int,
    val minutosJugados: Int,
    val goles: Int,
    val asistencias: Int,
    val tarjetasAmarillas: Int,
    val tarjetaRoja: Boolean,
)