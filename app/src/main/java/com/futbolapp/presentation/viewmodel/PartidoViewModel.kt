// presentation/viewmodel/PartidoViewModel.kt
package com.futbolapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futbolapp.domain.model.Partido
import com.futbolapp.domain.model.TemporadaStats
import com.futbolapp.domain.repository.PartidoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PartidosUiState(
    val partidos:   List<Partido>  = emptyList(),
    val stats:      TemporadaStats? = null,
    val isLoading:  Boolean        = false,
    val error:      String?        = null,
)

@HiltViewModel
class PartidoViewModel @Inject constructor(
    private val partidoRepository: PartidoRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PartidosUiState())
    val state: StateFlow<PartidosUiState> = _state.asStateFlow()

    init { loadPartidos() }

    fun loadPartidos() {
        android.util.Log.d("DEBUG_APP", "Intentando cargar partidos...")
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            partidoRepository.getPartidos()
                .onSuccess { lista ->
                    android.util.Log.d("DEBUG_APP", "Éxito: Se recibieron ${lista.size} partidos")
                    _state.update { it.copy(partidos = lista, isLoading = false) }
                }
                .onFailure { e ->
                    android.util.Log.e("DEBUG_APP", "Error: ${e.message}")
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            partidoRepository.getStats()
                .onSuccess { stats ->
                    _state.update { it.copy(stats = stats) }
                }
        }
    }
}