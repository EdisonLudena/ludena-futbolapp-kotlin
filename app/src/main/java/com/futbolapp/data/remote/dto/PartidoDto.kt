// data/remote/dto/PartidoDto.kt
package com.futbolapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.futbolapp.domain.model.Partido
import com.futbolapp.domain.model.PartidoPayload
import com.futbolapp.domain.model.TemporadaStats

data class PartidoDto(
    val id:     Int,
    val rival:  String,
    val fecha:  String,
    val lugar:  String,
    @SerializedName("resultado_final") val resultadoFinal: String?,
    @SerializedName("is_active")       val isActive:       Boolean,
)

data class PartidoRequestDto(
    val rival:  String,
    val fecha:  String,
    val lugar:  String,
    @SerializedName("is_active") val isActive: Boolean,
)

data class TemporadaStatsDto(
    @SerializedName("total_partidos")  val totalPartidos:  Int,
    val victorias:                                         Int,
    val derrotas:                                          Int,
    val empates:                                           Int,
    @SerializedName("goles_favor")    val golesFavor:     Int,
    @SerializedName("goles_contra")   val golesContra:    Int,
)

// ── Mappers ───────────────────────────────────────────────────

fun PartidoDto.toDomain() = Partido(
    id             = id,
    rival          = rival,
    fecha          = fecha,
    lugar          = lugar,
    resultadoFinal = resultadoFinal,
    isActive       = isActive,
)

fun PartidoPayload.toRequest() = PartidoRequestDto(
    rival    = rival,
    fecha    = fecha,
    lugar    = lugar,
    isActive = isActive,
)

fun TemporadaStatsDto.toDomain() = TemporadaStats(
    totalPartidos = totalPartidos,
    victorias     = victorias,
    derrotas      = derrotas,
    empates       = empates,
    golesFavor    = golesFavor,
    golesContra   = golesContra,
)