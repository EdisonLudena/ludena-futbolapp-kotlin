// data/remote/dto/EvaluacionDto.kt
package com.futbolapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.futbolapp.domain.model.Evaluacion
import com.futbolapp.domain.model.EvaluacionPayload

data class EvaluacionDto(
    val id:                                              Int,
    val jugador:                                         Int,
    @SerializedName("peso_kg")              val pesoKg:              String,  // Django devuelve Decimal como String
    @SerializedName("altura_cm")            val alturaCm:            Int,
    @SerializedName("velocidad_seg")        val velocidadSeg:        String,
    @SerializedName("calificacion_tecnica") val calificacionTecnica: Int,
    @SerializedName("notas_comentario")     val notasComentario:     String,
    @SerializedName("fecha_registro")       val fechaRegistro:       String,
)

data class EvaluacionRequestDto(
    val jugador:                                          Int,
    @SerializedName("peso_kg")              val pesoKg:              Double,
    @SerializedName("altura_cm")            val alturaCm:            Int,
    @SerializedName("velocidad_seg")        val velocidadSeg:        Double,
    @SerializedName("calificacion_tecnica") val calificacionTecnica: Int,
    @SerializedName("notas_comentario")     val notasComentario:     String,
)

// ── Mappers ───────────────────────────────────────────────────

fun EvaluacionDto.toDomain() = Evaluacion(
    id                  = id,
    jugadorId           = jugador,
    pesoKg              = pesoKg.toDoubleOrNull() ?: 0.0,
    alturaCm            = alturaCm,
    velocidadSeg        = velocidadSeg.toDoubleOrNull() ?: 0.0,
    calificacionTecnica = calificacionTecnica,
    notasComentario     = notasComentario,
    fechaRegistro       = fechaRegistro,
)

fun EvaluacionPayload.toRequest() = EvaluacionRequestDto(
    jugador             = jugador,
    pesoKg              = pesoKg,
    alturaCm            = alturaCm,
    velocidadSeg        = velocidadSeg,
    calificacionTecnica = calificacionTecnica,
    notasComentario     = notasComentario,
)