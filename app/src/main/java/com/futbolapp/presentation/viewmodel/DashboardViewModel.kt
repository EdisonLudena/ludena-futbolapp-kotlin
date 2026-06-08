// presentation/viewmodel/DashboardViewModel.kt
package com.futbolapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futbolapp.domain.model.Jugador
import com.futbolapp.domain.model.TemporadaStats
import com.futbolapp.domain.repository.EvaluacionRepository
import com.futbolapp.domain.repository.JugadorRepository
import com.futbolapp.domain.repository.PartidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardStats(
    // Jugadores
    val totalJugadores:      Int            = 0,
    val jugadoresActivos:    Int            = 0,
    val jugadoresInactivos:  Int            = 0,
    // Partidos
    val temporadaStats:      TemporadaStats? = null,
    val proximosPartidos:    Int            = 0,
    // Evaluaciones
    val totalEvaluaciones:   Int            = 0,
    val promedioCalificacion:Double         = 0.0,
    // Alertas
    val jugadoresSinEvaluacion: List<Jugador> = emptyList(),
)

sealed interface DashboardUiState {
    data object Loading                            : DashboardUiState
    data class  Success(val stats: DashboardStats) : DashboardUiState
    data class  Error(val message: String)         : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val jugadorRepository:    JugadorRepository,
    private val partidoRepository:    PartidoRepository,
    private val evaluacionRepository: EvaluacionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    private val _lastUpdated = MutableStateFlow(0L)
    val lastUpdated: StateFlow<Long> = _lastUpdated.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = DashboardUiState.Loading
            try {
                // Llamadas en paralelo con async
                val jugadoresDeferred    = async { jugadorRepository.getJugadores() }
                val partidosDeferred     = async { partidoRepository.getPartidos() }
                val statsDeferred        = async { partidoRepository.getStats() }
                val evaluacionesDeferred = async { evaluacionRepository.getEvaluaciones() }

                val jugadores    = jugadoresDeferred.await().getOrThrow()
                val partidos     = partidosDeferred.await().getOrThrow()
                val stats        = statsDeferred.await().getOrNull()
                val evaluaciones = evaluacionesDeferred.await().getOrThrow()

                // Jugadores que NO tienen ninguna evaluación registrada
                val jugadoresConEvaluacion = evaluaciones.map { it.jugadorId }.toSet()
                val sinEvaluacion = jugadores
                    .filter { it.isActive && it.id !in jugadoresConEvaluacion }
                    .take(5)

                // Promedio de calificación técnica
                val promedio = if (evaluaciones.isNotEmpty())
                    evaluaciones.map { it.calificacionTecnica }.average()
                else 0.0

                val dashStats = DashboardStats(
                    totalJugadores       = jugadores.size,
                    jugadoresActivos     = jugadores.count { it.isActive },
                    jugadoresInactivos   = jugadores.count { !it.isActive },
                    temporadaStats       = stats,
                    proximosPartidos     = partidos.count { it.resultadoFinal == null && it.isActive },
                    totalEvaluaciones    = evaluaciones.size,
                    promedioCalificacion = promedio,
                    jugadoresSinEvaluacion = sinEvaluacion,
                )

                _state.value       = DashboardUiState.Success(dashStats)
                _lastUpdated.value = System.currentTimeMillis()

            } catch (e: Exception) {
                _state.value = DashboardUiState.Error(e.message ?: "Error al cargar el dashboard")
            }
        }
    }
}