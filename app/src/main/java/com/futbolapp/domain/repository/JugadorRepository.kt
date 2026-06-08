// domain/repository/JugadorRepository.kt
package com.futbolapp.domain.repository

import com.futbolapp.domain.model.Jugador
import com.futbolapp.domain.model.JugadorPayload

interface JugadorRepository {
    suspend fun getJugadores(): Result<List<Jugador>>
    suspend fun getJugador(id: Int): Result<Jugador>
    suspend fun createJugador(payload: JugadorPayload): Result<Jugador>
    suspend fun updateJugador(id: Int, payload: JugadorPayload): Result<Jugador>
    suspend fun desactivarJugador(id: Int): Result<Jugador>
    suspend fun deleteJugador(id: Int): Result<Unit>
}