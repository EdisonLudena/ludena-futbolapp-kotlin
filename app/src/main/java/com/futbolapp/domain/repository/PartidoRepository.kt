// domain/repository/PartidoRepository.kt
package com.futbolapp.domain.repository

import com.futbolapp.domain.model.Partido
import com.futbolapp.domain.model.PartidoPayload
import com.futbolapp.domain.model.TemporadaStats
import com.futbolapp.domain.model.RendimientoPayload
import com.futbolapp.domain.model.EventoRendimiento

interface PartidoRepository {
    suspend fun getPartidos(): Result<List<Partido>>
    suspend fun getPartido(id: Int): Result<Partido>
    suspend fun createPartido(payload: PartidoPayload): Result<Partido>
    suspend fun updatePartido(id: Int, payload: PartidoPayload): Result<Partido>
    suspend fun actualizarResultado(id: Int, resultado: String): Result<Partido>
    suspend fun deletePartido(id: Int): Result<Unit>
    suspend fun registrarRendimiento(partidoId: Int, payload: RendimientoPayload): Result<EventoRendimiento>
    suspend fun getStats(): Result<TemporadaStats>
}