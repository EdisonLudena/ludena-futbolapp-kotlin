// data/repository/EvaluacionRepositoryImpl.kt
package com.futbolapp.data.repository

import android.util.Log
import com.futbolapp.data.remote.api.EvaluacionApi
import com.futbolapp.data.remote.dto.toDomain
import com.futbolapp.data.remote.dto.toRequest
import com.futbolapp.domain.model.Evaluacion
import com.futbolapp.domain.model.EvaluacionPayload
import com.futbolapp.domain.repository.EvaluacionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EvaluacionRepositoryImpl @Inject constructor(
    private val api: EvaluacionApi,
) : EvaluacionRepository {

    override suspend fun getEvaluaciones(): Result<List<Evaluacion>> = runCatching {
        val response = api.getEvaluaciones()
        if (response.isSuccessful) {
            // Accedemos a .results del DTO paginado
            response.body()?.results?.map { it.toDomain() } ?: emptyList()
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getEvaluacion(id: Int): Result<Evaluacion> = runCatching {
        val response = api.getEvaluacion(id)
        if (response.isSuccessful) response.body()!!.toDomain()
        else error("Error ${response.code()}")
    }

    override suspend fun createEvaluacion(payload: EvaluacionPayload): Result<Evaluacion> =
        runCatching {
            val response = api.createEvaluacion(payload.toRequest())
            if (response.isSuccessful) response.body()!!.toDomain()
            else error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }

    override suspend fun updateNotas(id: Int, notas: String): Result<Evaluacion> = runCatching {
        // 1. Definimos solo el campo que queremos cambiar
        val campos = mapOf("notas_comentario" to notas)

        // 2. Imprimimos para depurar (Logcat)
        Log.d("DEBUG_API", "Actualizando solo notas para ID $id: $campos")

        // 3. Llamamos a la API una sola vez
        val response = api.patchEvaluacion(id, campos)

        if (response.isSuccessful) {
            response.body()!!.toDomain()
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun patchEvaluacion(id: Int, campos: Map<String, Any>): Result<Evaluacion> = runCatching {
        val response = api.patchEvaluacion(id, campos)
        if (response.isSuccessful) {
            response.body()!!.toDomain()
        } else {
            error("Error ${response.code()}: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun deleteEvaluacion(id: Int): Result<Unit> = runCatching {
        val response = api.deleteEvaluacion(id)
        if (!response.isSuccessful) error("Error ${response.code()}")
    }

    override suspend fun updateEvaluacion(id: Int, payload: EvaluacionPayload): Result<Evaluacion> {
        val campos = mapOf(
            "jugador" to payload.jugador,
            "peso_kg" to payload.pesoKg,
            "altura_cm" to payload.alturaCm,
            "velocidad_seg" to payload.velocidadSeg,
            "calificacion_tecnica" to payload.calificacionTecnica,
            "notas_comentario" to payload.notasComentario
        )

        val response = api.patchEvaluacion(id, campos)

        if (response.isSuccessful) {
            return Result.success(response.body()!!.toDomain())
        } else {
            // AQUÍ ESTÁ LA CLAVE: Lee el cuerpo del error
            val errorBody = response.errorBody()?.string()
            Log.e("DEBUG_API_ERROR", "Error del servidor: $errorBody")
            return Result.failure(Exception("Error ${response.code()}: $errorBody"))
        }
    }
}