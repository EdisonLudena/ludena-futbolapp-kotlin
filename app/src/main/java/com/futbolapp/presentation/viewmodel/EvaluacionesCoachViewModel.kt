// presentation/viewmodel/EvaluacionesCoachViewModel.kt
package com.futbolapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futbolapp.domain.model.Evaluacion
import com.futbolapp.domain.model.EvaluacionPayload
import com.futbolapp.domain.model.Jugador
import com.futbolapp.domain.repository.EvaluacionRepository
import com.futbolapp.domain.repository.JugadorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

enum class EvaluacionFiltro(val label: String) {
    TODOS("Todas"),
    EXCELENTE("Excelente ≥80"),
    BUENO("Bueno 60-79"),
    REGULAR("Regular 40-59"),
    BAJO("Bajo <40"),
}

data class EvaluacionesCoachUiState(
    val evaluaciones: List<Evaluacion> = emptyList(),
    val jugadores:    List<Jugador>    = emptyList(),
    val isLoading:    Boolean          = false,
    val error:        String?          = null,
    val total:        Int              = 0,
    val search:       String           = "",
    val filtro:       EvaluacionFiltro = EvaluacionFiltro.TODOS,
)

sealed interface EvaluacionCoachFormState {
    data object Idle                       : EvaluacionCoachFormState
    data object Saving                     : EvaluacionCoachFormState
    data class  Success(val msg: String)   : EvaluacionCoachFormState
    data class  Error(val message: String) : EvaluacionCoachFormState
}

@HiltViewModel
class EvaluacionesCoachViewModel @Inject constructor(
    private val evaluacionRepository: EvaluacionRepository,
    private val jugadorRepository:    JugadorRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(EvaluacionesCoachUiState())
    val state: StateFlow<EvaluacionesCoachUiState> = _state.asStateFlow()

    private val _formState = MutableStateFlow<EvaluacionCoachFormState>(EvaluacionCoachFormState.Idle)
    val formState: StateFlow<EvaluacionCoachFormState> = _formState.asStateFlow()

    // Lista filtrada reactiva — busca por nombre del jugador o calificación
    val filtered: StateFlow<List<Evaluacion>> = _state
        .map { s ->
            val jugadoresMap = s.jugadores.associateBy { it.id }
            s.evaluaciones
                .filter { ev ->
                    if (s.search.isBlank()) true
                    else {
                        val jugador = jugadoresMap[ev.jugadorId]
                        jugador?.let {
                            it.nombres.contains(s.search, ignoreCase = true) ||
                                    it.apellidos.contains(s.search, ignoreCase = true)
                        } ?: ev.jugadorId.toString().contains(s.search)
                    }
                }
                .filter { ev ->
                    when (s.filtro) {
                        EvaluacionFiltro.TODOS     -> true
                        EvaluacionFiltro.EXCELENTE -> ev.calificacionTecnica >= 80
                        EvaluacionFiltro.BUENO     -> ev.calificacionTecnica in 60..79
                        EvaluacionFiltro.REGULAR   -> ev.calificacionTecnica in 40..59
                        EvaluacionFiltro.BAJO      -> ev.calificacionTecnica < 40
                    }
                }
                .sortedByDescending { it.calificacionTecnica }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Mapa id → nombre para mostrar en la lista
    val jugadoresMap: StateFlow<Map<Int, Jugador>> = _state
        .map { it.jugadores.associateBy { j -> j.id } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    private var searchJob: Job? = null

    init {
        load()
        loadJugadores()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            evaluacionRepository.getEvaluaciones()
                .onSuccess { lista ->
                    _state.update { it.copy(
                        evaluaciones = lista,
                        total        = lista.size,
                        isLoading    = false,
                    ) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private fun loadJugadores() {
        viewModelScope.launch {
            jugadorRepository.getJugadores()
                .onSuccess { lista ->
                    _state.update { it.copy(jugadores = lista) }
                }
        }
    }

    fun setSearch(query: String) {
        _state.update { it.copy(search = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch { delay(300) }
    }

    fun setFiltro(filtro: EvaluacionFiltro) {
        _state.update { it.copy(filtro = filtro) }
    }

    // Eliminación optimista
    fun deleteEvaluacion(id: Int) {
        val backup = _state.value.evaluaciones.find { it.id == id }
        _state.update { s ->
            s.copy(
                evaluaciones = s.evaluaciones.filter { it.id != id },
                total        = s.total - 1,
            )
        }
        viewModelScope.launch {
            evaluacionRepository.deleteEvaluacion(id)
                .onFailure { e ->
                    // Revertir si falla
                    backup?.let { ev ->
                        _state.update { s ->
                            s.copy(
                                evaluaciones = (s.evaluaciones + ev).sortedByDescending { it.id },
                                total        = s.total + 1,
                                error        = e.message,
                            )
                        }
                    }
                }
        }
    }

    fun updateEvaluacion(id: Int, payload: EvaluacionPayload) {
        _formState.value = EvaluacionCoachFormState.Saving
        viewModelScope.launch {
            // Llamamos al repositorio
            evaluacionRepository.updateEvaluacion(id, payload)
                .onSuccess { actualizado ->
                    // Actualizamos la lista localmente
                    _state.update { s ->
                        s.copy(
                            evaluaciones = s.evaluaciones.map { if (it.id == id) actualizado else it }
                        )
                    }
                    _formState.value = EvaluacionCoachFormState.Success("Evaluación actualizada")
                }
                .onFailure { e ->
                    _formState.value = EvaluacionCoachFormState.Error(e.message ?: "Error al actualizar")
                }
        }
    }

    fun createEvaluacion(payload: EvaluacionPayload) {
        _formState.value = EvaluacionCoachFormState.Saving
        viewModelScope.launch {
            evaluacionRepository.createEvaluacion(payload)
                .onSuccess { creado ->
                    // Agregamos la nueva evaluación al principio de la lista y actualizamos el total
                    _state.update { s ->
                        s.copy(
                            evaluaciones = listOf(creado) + s.evaluaciones,
                            total        = s.total + 1
                        )
                    }
                    _formState.value = EvaluacionCoachFormState.Success("Evaluación guardada")
                }
                .onFailure { e ->
                    _formState.value = EvaluacionCoachFormState.Error(e.message ?: "Error al guardar")
                }
        }
    }

    fun resetFormState() { _formState.value = EvaluacionCoachFormState.Idle }
}