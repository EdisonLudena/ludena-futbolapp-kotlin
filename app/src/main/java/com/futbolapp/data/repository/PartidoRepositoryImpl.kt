// data/repository/PartidoRepositoryImpl.kt
package com.futbolapp.data.repository

import com.futbolapp.data.remote.api.PartidoApi
import com.futbolapp.data.remote.dto.toDomain
import com.futbolapp.data.remote.dto.toRequest
import com.futbolapp.domain.model.*
import com.futbolapp.domain.repository.PartidoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PartidoRepositoryImpl @Inject constructor(
    private val api: PartidoApi,
) : PartidoRepository {

    override suspend fun getPartidos(): Result<List<Partido>> = runCatching {
        val response = api.getPartidos()
        if (response.isSuccessful) {
            // response.body() ahora es un PaginatedDto, así que accedemos a .results
            response.body()?.results?.map { it.toDomain() } ?: emptyList()
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getPartido(id: Int): Result<Partido> = runCatching {
        val response = api.getPartido(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun createPartido(payload: PartidoPayload): Result<Partido> = runCatching {
        val response = api.createPartido(payload.toRequest())
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun updatePartido(id: Int, payload: PartidoPayload): Result<Partido> =
        runCatching {
            val response = api.updatePartido(id, payload.toRequest())
            if (response.isSuccessful) response.body()!!.toDomain()
            else error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }

    override suspend fun actualizarResultado(id: Int, resultado: String): Result<Partido> =
        runCatching {
            val response = api.patchPartido(id, mapOf("resultado_final" to resultado))
            if (response.isSuccessful) response.body()!!.toDomain()
            else error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }

    override suspend fun deletePartido(id: Int): Result<Unit> = runCatching {
        val response = api.deletePartido(id)
        if (!response.isSuccessful) error("Error ${response.code()}")
    }

    override suspend fun registrarRendimiento(
        partidoId: Int,
        payload: RendimientoPayload,
    ): Result<EventoRendimiento> = runCatching {
        val response = api.registrarRendimiento(partidoId, payload.toRequest())
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun getStats(): Result<TemporadaStats> = runCatching {
        val response = api.getStats()
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }
}