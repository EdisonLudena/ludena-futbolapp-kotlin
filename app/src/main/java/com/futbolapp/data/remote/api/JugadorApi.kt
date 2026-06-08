// data/remote/api/JugadorApi.kt
package com.futbolapp.data.remote.api

import com.futbolapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface JugadorApi {
    @GET("players/")
    suspend fun getJugadores(): Response<PaginatedDto<JugadorDto>>

    @GET("players/{id}/")
    suspend fun getJugador(@Path("id") id: Int): Response<JugadorDto>

    @POST("players/")
    suspend fun createJugador(@Body body: JugadorRequestDto): Response<JugadorDto>

    @PUT("players/{id}/")
    suspend fun updateJugador(
        @Path("id") id: Int,
        @Body body: JugadorRequestDto,
    ): Response<JugadorDto>

    @PATCH("players/{id}/")
    suspend fun patchJugador(
        @Path("id") id: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>,
    ): Response<JugadorDto>

    @DELETE("players/{id}/")
    suspend fun deleteJugador(@Path("id") id: Int): Response<Unit>

    @GET("players/{id}/partidos/")
    suspend fun getPartidosDeJugador(@Path("id") id: Int): Response<List<EventoRendimientoDto>>
}