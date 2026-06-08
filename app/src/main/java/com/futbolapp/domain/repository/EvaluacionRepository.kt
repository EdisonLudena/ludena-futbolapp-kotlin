// domain/repository/EvaluacionRepository.kt
package com.futbolapp.domain.repository

import com.futbolapp.domain.model.Evaluacion
import com.futbolapp.domain.model.EvaluacionPayload

interface EvaluacionRepository {
    suspend fun getEvaluaciones(): Result<List<Evaluacion>>
    suspend fun getEvaluacion(id: Int): Result<Evaluacion>
    suspend fun createEvaluacion(payload: EvaluacionPayload): Result<Evaluacion>
    suspend fun updateEvaluacion(id: Int, payload: EvaluacionPayload): Result<Evaluacion>
    suspend fun updateNotas(id: Int, notas: String): Result<Evaluacion>

    suspend fun patchEvaluacion(id: Int, campos: Map<String, Any>): Result<Evaluacion>
    suspend fun deleteEvaluacion(id: Int): Result<Unit>
}