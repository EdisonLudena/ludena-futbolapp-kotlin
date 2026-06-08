package com.futbolapp.domain.model

data class Evaluacion(
    val id: Int,
    val jugadorId: Int,
    val pesoKg: Double,
    val alturaCm: Int,
    val velocidadSeg: Double,
    val calificacionTecnica: Int,   // 0–100
    val notasComentario: String,
    val fechaRegistro: String,
)

data class EvaluacionPayload(
    val jugador: Int,
    val pesoKg: Double,
    val alturaCm: Int,
    val velocidadSeg: Double,
    val calificacionTecnica: Int,
    val notasComentario: String,
)