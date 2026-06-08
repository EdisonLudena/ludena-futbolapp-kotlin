// data/remote/api/EvaluacionApi.kt
package com.futbolapp.data.remote.api

import com.futbolapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface EvaluacionApi {
    @GET("evaluations/")
    suspend fun getEvaluaciones(): Response<PaginatedDto<EvaluacionDto>>

    @GET("evaluations/{id}/")
    suspend fun getEvaluacion(@Path("id") id: Int): Response<EvaluacionDto>

    @POST("evaluations/")
    suspend fun createEvaluacion(@Body body: EvaluacionRequestDto): Response<EvaluacionDto>

    @PATCH("evaluations/{id}/")
    suspend fun patchEvaluacion(
        @Path("id") id: Int,
        @Body body: Map<String, @JvmSuppressWildcards Any>,
    ): Response<EvaluacionDto>

    @PUT("evaluations/{id}/") // O @PATCH dependiendo de cómo esté configurado en tu Django
    suspend fun updateEvaluacion(
        @Path("id") id: Int,
        @Body body: EvaluacionRequestDto
    ): Response<EvaluacionDto>

    @DELETE("evaluations/{id}/")
    suspend fun deleteEvaluacion(@Path("id") id: Int): Response<Unit>
}