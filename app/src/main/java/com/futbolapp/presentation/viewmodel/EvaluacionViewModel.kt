// presentation/viewmodel/EvaluacionViewModel.kt
package com.futbolapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futbolapp.domain.model.Evaluacion
import com.futbolapp.domain.model.EvaluacionPayload
import com.futbolapp.domain.repository.EvaluacionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EvaluacionesUiState(
    val evaluaciones: List<Evaluacion> = emptyList(),
    val isLoading:    Boolean          = false,
    val error:        String?          = null,
)

sealed interface EvaluacionDetalleUiState {
    data object Loading                           : EvaluacionDetalleUiState
    data class  Success(val ev: Evaluacion)       : EvaluacionDetalleUiState
    data class  Error(val message: String)        : EvaluacionDetalleUiState
}

sealed interface CrearEvaluacionState {
    data object Idle                       : CrearEvaluacionState
    data object Loading                    : CrearEvaluacionState
    data class  Success(val ev: Evaluacion): CrearEvaluacionState
    data class  Error(val message: String) : CrearEvaluacionState
}

sealed interface EdicionEvaluacionState {
    data object Idle                       : EdicionEvaluacionState
    data object Loading                    : EdicionEvaluacionState
    data class  Success(val ev: Evaluacion): EdicionEvaluacionState
    data class  Error(val message: String) : EdicionEvaluacionState
}

@HiltViewModel
class EvaluacionViewModel @Inject constructor(
    private val repository: EvaluacionRepository,
) : ViewModel() {

    // ── Lista ─────────────────────────────────────────────────
    private val _listState = MutableStateFlow(EvaluacionesUiState())
    val listState: StateFlow<EvaluacionesUiState> = _listState.asStateFlow()

    // ── Detalle ───────────────────────────────────────────────
    private val _detalleState =
        MutableStateFlow<EvaluacionDetalleUiState>(EvaluacionDetalleUiState.Loading)
    val detalleState: StateFlow<EvaluacionDetalleUiState> = _detalleState.asStateFlow()

    // ── Crear ─────────────────────────────────────────────────
    private val _crearState = MutableStateFlow<CrearEvaluacionState>(CrearEvaluacionState.Idle)
    val crearState: StateFlow<CrearEvaluacionState> = _crearState.asStateFlow()

    init { loadEvaluaciones() }

    fun loadEvaluaciones() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            repository.getEvaluaciones()
                .onSuccess { lista ->
                    _listState.update { it.copy(evaluaciones = lista, isLoading = false) }
                }
                .onFailure { e ->
                    _listState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun loadDetalle(id: Int) {
        viewModelScope.launch {
            _detalleState.value = EvaluacionDetalleUiState.Loading
            repository.getEvaluacion(id)
                .onSuccess { _detalleState.value = EvaluacionDetalleUiState.Success(it) }
                .onFailure { _detalleState.value = EvaluacionDetalleUiState.Error(it.message ?: "Error") }
        }
    }

    fun crearEvaluacion(payload: EvaluacionPayload) {
        viewModelScope.launch {
            _crearState.value = CrearEvaluacionState.Loading
            repository.createEvaluacion(payload)
                .onSuccess { ev ->
                    _crearState.value = CrearEvaluacionState.Success(ev)
                    // Refrescar la lista automáticamente
                    loadEvaluaciones()
                }
                .onFailure { _crearState.value = CrearEvaluacionState.Error(it.message ?: "Error") }
        }
    }

    fun deleteEvaluacion(id: Int, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.deleteEvaluacion(id)
                .onSuccess {
                    loadEvaluaciones()
                    onDone()
                }
        }
    }

    // ── Editar ─────────────────────────────────────────────────
    private val _editState = MutableStateFlow<EdicionEvaluacionState>(EdicionEvaluacionState.Idle)
    val editState: StateFlow<EdicionEvaluacionState> = _editState.asStateFlow()

    fun updateEvaluacion(id: Int, payload: EvaluacionPayload) {
        viewModelScope.launch {
            _editState.value = EdicionEvaluacionState.Loading
            repository.updateEvaluacion(id, payload)
                .onSuccess { ev ->
                    _editState.value = EdicionEvaluacionState.Success(ev)
                    loadEvaluaciones() // Refresca la lista tras editar
                }
                .onFailure { e ->
                    _editState.value = EdicionEvaluacionState.Error(e.message ?: "Error al actualizar")
                }
        }
    }

    fun clearEditState() { _editState.value = EdicionEvaluacionState.Idle }

    fun clearCrearState() { _crearState.value = CrearEvaluacionState.Idle }
}