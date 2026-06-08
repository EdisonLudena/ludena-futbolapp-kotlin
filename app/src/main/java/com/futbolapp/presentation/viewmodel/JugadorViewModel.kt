// presentation/viewmodel/JugadorViewModel.kt
package com.futbolapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futbolapp.domain.model.Jugador
import com.futbolapp.domain.repository.JugadorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JugadoresUiState(
    val jugadores:         List<Jugador> = emptyList(),
    val jugadoresFiltrados: List<Jugador> = emptyList(),
    val isLoading:         Boolean = false,
    val error:             String? = null,
    val search:            String  = "",
    val filtroCategoria:   String? = null,
    val filtroPosicion:    String? = null,
)

@HiltViewModel
class JugadorViewModel @Inject constructor(
    private val jugadorRepository: JugadorRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(JugadoresUiState())
    val state: StateFlow<JugadoresUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init { loadJugadores() }

    fun loadJugadores() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            jugadorRepository.getJugadores()
                .onSuccess { lista ->
                    _state.update { it.copy(
                        jugadores          = lista,
                        jugadoresFiltrados = lista,
                        isLoading          = false,
                    ) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun setSearch(query: String) {
        _state.update { it.copy(search = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            aplicarFiltros()
        }
    }

    fun setFiltroCategoria(categoria: String?) {
        _state.update { it.copy(filtroCategoria = categoria) }
        aplicarFiltros()
    }

    fun setFiltroPosicion(posicion: String?) {
        _state.update { it.copy(filtroPosicion = posicion) }
        aplicarFiltros()
    }

    fun limpiarFiltros() {
        _state.update { it.copy(search = "", filtroCategoria = null, filtroPosicion = null) }
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val s = _state.value
        val filtrados = s.jugadores.filter { jugador ->
            val matchSearch = s.search.isBlank() ||
                    jugador.nombres.contains(s.search, ignoreCase = true) ||
                    jugador.apellidos.contains(s.search, ignoreCase = true) ||
                    jugador.posicion.contains(s.search, ignoreCase = true)
            val matchCategoria = s.filtroCategoria == null ||
                    jugador.categoria.equals(s.filtroCategoria, ignoreCase = true)
            val matchPosicion  = s.filtroPosicion == null ||
                    jugador.posicion.equals(s.filtroPosicion, ignoreCase = true)
            matchSearch && matchCategoria && matchPosicion
        }
        _state.update { it.copy(jugadoresFiltrados = filtrados) }
    }
}