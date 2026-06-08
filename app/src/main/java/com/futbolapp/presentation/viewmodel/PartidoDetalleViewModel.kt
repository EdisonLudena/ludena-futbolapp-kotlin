// presentation/viewmodel/PartidoDetalleViewModel.kt
package com.futbolapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futbolapp.domain.model.Jugador
import com.futbolapp.domain.model.Partido
import com.futbolapp.domain.model.RendimientoPayload
import com.futbolapp.domain.repository.JugadorRepository
import com.futbolapp.domain.repository.PartidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PartidoDetalleUiState {
    data object Loading                       : PartidoDetalleUiState
    data class  Success(val partido: Partido) : PartidoDetalleUiState
    data class  Error(val message: String)    : PartidoDetalleUiState
}

sealed interface FormState {
    data object Idle                       : FormState
    data object Loading                    : FormState
    data class  Success(val msg: String)   : FormState
    data class  Error(val message: String) : FormState
}

@HiltViewModel
class PartidoDetalleViewModel @Inject constructor(
    private val partidoRepository: PartidoRepository,
    private val jugadorRepository: JugadorRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<PartidoDetalleUiState>(PartidoDetalleUiState.Loading)
    val state: StateFlow<PartidoDetalleUiState> = _state.asStateFlow()

    private val _jugadores = MutableStateFlow<List<Jugador>>(emptyList())
    val jugadores: StateFlow<List<Jugador>> = _jugadores.asStateFlow()

    private val _rendimientoForm = MutableStateFlow<FormState>(FormState.Idle)
    val rendimientoForm: StateFlow<FormState> = _rendimientoForm.asStateFlow()

    private val _resultadoForm = MutableStateFlow<FormState>(FormState.Idle)
    val resultadoForm: StateFlow<FormState> = _resultadoForm.asStateFlow()

    fun load(id: Int) {
        viewModelScope.launch {
            _state.value = PartidoDetalleUiState.Loading
            partidoRepository.getPartido(id)
                .onSuccess { _state.value = PartidoDetalleUiState.Success(it) }
                .onFailure { _state.value = PartidoDetalleUiState.Error(it.message ?: "Error") }
        }
        viewModelScope.launch {
            jugadorRepository.getJugadores()
                .onSuccess { _jugadores.value = it.filter { j -> j.isActive } }
        }
    }

    fun registrarRendimiento(partidoId: Int, payload: RendimientoPayload) {
        viewModelScope.launch {
            _rendimientoForm.value = FormState.Loading
            partidoRepository.registrarRendimiento(partidoId, payload)
                .onSuccess { _rendimientoForm.value = FormState.Success("Rendimiento registrado ✓") }
                .onFailure { _rendimientoForm.value = FormState.Error(it.message ?: "Error") }
        }
    }

    fun actualizarResultado(partido: Partido, resultado: String) {
        viewModelScope.launch {
            _resultadoForm.value = FormState.Loading
            partidoRepository.actualizarResultado(partido.id, resultado)
                .onSuccess {
                    _state.value         = PartidoDetalleUiState.Success(it)
                    _resultadoForm.value = FormState.Success("Resultado guardado ✓")
                }
                .onFailure { _resultadoForm.value = FormState.Error(it.message ?: "Error") }
        }
    }

    fun clearRendimientoForm() { _rendimientoForm.value = FormState.Idle }
    fun clearResultadoForm()   { _resultadoForm.value   = FormState.Idle }
}