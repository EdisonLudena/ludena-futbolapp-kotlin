// presentation/viewmodel/JugadorDetalleViewModel.kt
package com.futbolapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futbolapp.domain.model.Jugador
import com.futbolapp.domain.repository.JugadorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface JugadorDetalleUiState {
    data object Loading                       : JugadorDetalleUiState
    data class  Success(val jugador: Jugador) : JugadorDetalleUiState
    data class  Error(val message: String)    : JugadorDetalleUiState
}

@HiltViewModel
class JugadorDetalleViewModel @Inject constructor(
    private val jugadorRepository: JugadorRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<JugadorDetalleUiState>(JugadorDetalleUiState.Loading)
    val state: StateFlow<JugadorDetalleUiState> = _state.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    fun load(id: Int) {
        viewModelScope.launch {
            _state.value = JugadorDetalleUiState.Loading
            jugadorRepository.getJugador(id)
                .onSuccess { _state.value = JugadorDetalleUiState.Success(it) }
                .onFailure { _state.value = JugadorDetalleUiState.Error(it.message ?: "Error") }
        }
    }

    fun toggleActivo(jugador: Jugador, onDone: () -> Unit) {
        viewModelScope.launch {
            jugadorRepository.desactivarJugador(jugador.id)
                .onSuccess {
                    _state.value = JugadorDetalleUiState.Success(it)
                    onDone()
                }
        }
    }

    fun delete(jugador: Jugador, onDone: () -> Unit) {
        viewModelScope.launch {
            _isDeleting.value = true
            jugadorRepository.deleteJugador(jugador.id)
                .onSuccess { onDone() }
                .onFailure { _isDeleting.value = false }
        }
    }
}