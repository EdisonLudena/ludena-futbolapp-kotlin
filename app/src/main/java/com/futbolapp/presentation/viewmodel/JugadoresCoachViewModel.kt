// presentation/viewmodel/JugadoresCoachViewModel.kt
package com.futbolapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futbolapp.domain.model.Jugador
import com.futbolapp.domain.model.JugadorPayload
import com.futbolapp.domain.repository.JugadorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JugadoresCoachUiState(
    val jugadores: List<Jugador> = emptyList(),
    val isLoading: Boolean       = false,
    val error:     String?       = null,
    val search:    String        = "",
)

sealed interface JugadorFormState {
    data object Idle                       : JugadorFormState
    data object Saving                     : JugadorFormState
    data class  Success(val msg: String)   : JugadorFormState
    data class  Error(val message: String) : JugadorFormState
}

@HiltViewModel
class JugadoresCoachViewModel @Inject constructor(
    private val repository: JugadorRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(JugadoresCoachUiState())
    val state: StateFlow<JugadoresCoachUiState> = _state.asStateFlow()

    private val _formState = MutableStateFlow<JugadorFormState>(JugadorFormState.Idle)
    val formState: StateFlow<JugadorFormState> = _formState.asStateFlow()

    // Lista filtrada reactiva
    val filtered: StateFlow<List<Jugador>> = _state
        .map { s ->
            if (s.search.isBlank()) s.jugadores
            else s.jugadores.filter {
                it.nombres.contains(s.search, ignoreCase = true) ||
                        it.apellidos.contains(s.search, ignoreCase = true) ||
                        it.posicion.contains(s.search, ignoreCase = true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getJugadores()
                .onSuccess { lista ->
                    _state.update { it.copy(jugadores = lista, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun setSearch(query: String) {
        _state.update { it.copy(search = query) }
    }

    // Toggle optimista: actualiza la UI antes de confirmar con el servidor
    fun toggleActivo(id: Int, isActive: Boolean) {
        _state.update { s ->
            s.copy(jugadores = s.jugadores.map {
                if (it.id == id) it.copy(isActive = isActive) else it
            })
        }
        viewModelScope.launch {
            repository.desactivarJugador(id)
                .onFailure {
                    // Revertir si falla
                    _state.update { s ->
                        s.copy(jugadores = s.jugadores.map { j ->
                            if (j.id == id) j.copy(isActive = !isActive) else j
                        })
                    }
                }
        }
    }

    fun createJugador(payload: JugadorPayload) {
        _formState.value = JugadorFormState.Saving
        viewModelScope.launch {
            repository.createJugador(payload)
                .onSuccess { creado ->
                    _state.update { s ->
                        s.copy(jugadores = listOf(creado) + s.jugadores)
                    }
                    _formState.value = JugadorFormState.Success("Jugador creado")
                }
                .onFailure { e ->
                    _formState.value = JugadorFormState.Error(e.message ?: "Error al crear")
                }
        }
    }

    fun updateJugador(id: Int, payload: JugadorPayload) {
        _formState.value = JugadorFormState.Saving
        viewModelScope.launch {
            repository.updateJugador(id, payload)
                .onSuccess { actualizado ->
                    _state.update { s ->
                        s.copy(jugadores = s.jugadores.map {
                            if (it.id == id) actualizado else it
                        })
                    }
                    _formState.value = JugadorFormState.Success("Jugador actualizado")
                }
                .onFailure { e ->
                    _formState.value = JugadorFormState.Error(e.message ?: "Error al actualizar")
                }
        }
    }

    fun deleteJugador(id: Int) {
        viewModelScope.launch {
            repository.deleteJugador(id)
                .onSuccess {
                    _state.update { s ->
                        s.copy(jugadores = s.jugadores.filter { it.id != id })
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message) }
                }
        }
    }

    fun resetFormState() { _formState.value = JugadorFormState.Idle }
}