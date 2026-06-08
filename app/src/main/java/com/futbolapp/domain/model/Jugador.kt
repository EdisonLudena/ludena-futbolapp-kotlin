package com.futbolapp.domain.model

data class Jugador(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val categoria: String,     // "Senior", "Juvenil", etc.
    val posicion: String,      // "Delantero", "Portero", etc.
    val fotoUrl: String?,
    val isActive: Boolean,
)

data class JugadorPayload(
    val nombres: String,
    val apellidos: String,
    val categoria: String,
    val posicion: String,
    val fotoUrl: String?,
    val isActive: Boolean,
)