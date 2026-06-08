// data/remote/api/PartidoApi.kt
package com.futbolapp.data.remote.api

import com.futbolapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface PartidoApi {
    @GET("matches/")
    suspend fun getPartidos(): Response<PaginatedDto<PartidoDto>>

    @GET("matches/{id}/")
    suspend fun getPartido(@Path("id") id: Int): Response<PartidoDto>

    @POST("matches/")
    suspend fun createPartido(@Body body: PartidoRequestDto): Response<PartidoDto>

    @PUT("matches/{id}/")
    suspend fun updatePartido(
        @Path("id") id: Int,
        @Body body: PartidoRequestDto,
    ): Response<PartidoDto>

    @PATCH("matches/{id}/")
    suspend fun patchPartido(
        @Path("id") id: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>,
    ): Response<PartidoDto>

    @DELETE("matches/{id}/")
    suspend fun deletePartido(@Path("id") id: Int): Response<Unit>

    @POST("matches/{id}/registrar-rendimiento/")
    suspend fun registrarRendimiento(
        @Path("id") partidoId: Int,
        @Body body: RendimientoRequestDto,
    ): Response<EventoRendimientoDto>

    @GET("matches/stats/")
    suspend fun getStats(): Response<TemporadaStatsDto>
}