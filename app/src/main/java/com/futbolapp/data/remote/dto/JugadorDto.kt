// data/remote/dto/JugadorDto.kt
package com.futbolapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.futbolapp.domain.model.Jugador
import com.futbolapp.domain.model.JugadorPayload

data class JugadorDto(
    val id:        Int,
    val nombres:   String,
    val apellidos: String,
    val categoria: String,
    val posicion:  String,
    @SerializedName("foto_url")  val fotoUrl:  String?,
    @SerializedName("is_active") val isActive: Boolean,
)

data class JugadorRequestDto(
    val nombres:   String,
    val apellidos: String,
    val categoria: String,
    val posicion:  String,
    @SerializedName("foto_url")  val fotoUrl:  String?,
    @SerializedName("is_active") val isActive: Boolean,
)

// ── Mappers ───────────────────────────────────────────────────

fun JugadorDto.toDomain() = Jugador(
    id        = id,
    nombres   = nombres,
    apellidos = apellidos,
    categoria = categoria,
    posicion  = posicion,
    fotoUrl   = fotoUrl,
    isActive  = isActive,
)

fun JugadorPayload.toRequest() = JugadorRequestDto(
    nombres   = nombres,
    apellidos = apellidos,
    categoria = categoria,
    posicion  = posicion,
    fotoUrl   = fotoUrl,
    isActive  = isActive,
)