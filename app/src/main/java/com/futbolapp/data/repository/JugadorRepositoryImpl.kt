// data/repository/JugadorRepositoryImpl.kt
package com.futbolapp.data.repository

import com.futbolapp.data.remote.api.JugadorApi
import com.futbolapp.data.remote.dto.toDomain
import com.futbolapp.data.remote.dto.toRequest
import com.futbolapp.domain.model.Jugador
import com.futbolapp.domain.model.JugadorPayload
import com.futbolapp.domain.repository.JugadorRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JugadorRepositoryImpl @Inject constructor(
    private val api: JugadorApi,
) : JugadorRepository {

    override suspend fun getJugadores(): Result<List<Jugador>> = runCatching {
        val response = api.getJugadores()
        if (response.isSuccessful) {
            // Accedemos a .results del DTO paginado
            response.body()?.results?.map { it.toDomain() } ?: emptyList()
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getJugador(id: Int): Result<Jugador> = runCatching {
        val response = api.getJugador(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun createJugador(payload: JugadorPayload): Result<Jugador> = runCatching {
        val response = api.createJugador(payload.toRequest())
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun updateJugador(id: Int, payload: JugadorPayload): Result<Jugador> =
        runCatching {
            val response = api.updateJugador(id, payload.toRequest())
            if (response.isSuccessful) response.body()!!.toDomain()
            else error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }

    override suspend fun desactivarJugador(id: Int): Result<Jugador> = runCatching {
        val response = api.patchJugador(id, mapOf("is_active" to false))
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}: ${response.errorBody()?.string()}")
    }

    override suspend fun deleteJugador(id: Int): Result<Unit> = runCatching {
        val response = api.deleteJugador(id)
        if (!response.isSuccessful) error("Error ${response.code()}")
    }
}