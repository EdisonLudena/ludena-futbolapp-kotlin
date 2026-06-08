// data/remote/dto/RendimientoDto.kt
package com.futbolapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.futbolapp.domain.model.EventoRendimiento
import com.futbolapp.domain.model.RendimientoPayload

data class EventoRendimientoDto(
    val id:                                             Int,
    @SerializedName("jugador_id")          val jugadorId:          Int,
    @SerializedName("partido_id")          val partidoId:          Int,
    @SerializedName("minutos_jugados")     val minutosJugados:     Int,
    val goles:                                          Int,
    val asistencias:                                    Int,
    @SerializedName("tarjetas_amarillas")  val tarjetasAmarillas:  Int,
    @SerializedName("tarjeta_roja")        val tarjetaRoja:        Boolean,
)

data class RendimientoRequestDto(
    @SerializedName("jugador_id")         val jugadorId:         Int,
    @SerializedName("minutos_jugados")    val minutosJugados:    Int,
    val goles:                                                    Int,
    val asistencias:                                              Int,
    @SerializedName("tarjetas_amarillas") val tarjetasAmarillas: Int,
    @SerializedName("tarjeta_roja")       val tarjetaRoja:       Boolean,
)

// ── Mappers ───────────────────────────────────────────────────

fun EventoRendimientoDto.toDomain() = EventoRendimiento(
    id                = id,
    jugadorId         = jugadorId,
    partidoId         = partidoId,
    minutosJugados    = minutosJugados,
    goles             = goles,
    asistencias       = asistencias,
    tarjetasAmarillas = tarjetasAmarillas,
    tarjetaRoja       = tarjetaRoja,
)

fun RendimientoPayload.toRequest() = RendimientoRequestDto(
    jugadorId         = jugadorId,
    minutosJugados    = minutosJugados,
    goles             = goles,
    asistencias       = asistencias,
    tarjetasAmarillas = tarjetasAmarillas,
    tarjetaRoja       = tarjetaRoja,
)