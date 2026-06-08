    // presentation/viewmodel/PartidosCoachViewModel.kt
    package com.futbolapp.presentation.viewmodel

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.futbolapp.domain.model.Partido
    import com.futbolapp.domain.model.PartidoPayload
    import com.futbolapp.domain.repository.PartidoRepository
    import dagger.hilt.android.lifecycle.HiltViewModel
    import kotlinx.coroutines.flow.*
    import kotlinx.coroutines.launch
    import javax.inject.Inject

    enum class PartidoFiltro(val label: String) {
        TODOS("Todos"),
        PROXIMOS("Próximos"),
        FINALIZADOS("Finalizados"),
        ACTIVOS("Activos"),
        INACTIVOS("Inactivos"),
    }

    data class PartidosCoachUiState(
        val partidos: List<Partido>  = emptyList(),
        val isLoading: Boolean       = false,
        val error:     String?       = null,
        val search:    String        = "",
        val filtro:    PartidoFiltro = PartidoFiltro.TODOS,
    )

    sealed interface PartidoFormState {
        data object Idle                       : PartidoFormState
        data object Saving                     : PartidoFormState
        data class  Success(val msg: String)   : PartidoFormState
        data class  Error(val message: String) : PartidoFormState
    }

    @HiltViewModel
    class PartidosCoachViewModel @Inject constructor(
        private val repository: PartidoRepository,
    ) : ViewModel() {

        private val _state = MutableStateFlow(PartidosCoachUiState())
        val state: StateFlow<PartidosCoachUiState> = _state.asStateFlow()

        private val _formState = MutableStateFlow<PartidoFormState>(PartidoFormState.Idle)
        val formState: StateFlow<PartidoFormState> = _formState.asStateFlow()

        // Lista filtrada reactiva
        val filtered: StateFlow<List<Partido>> = _state
            .map { s ->
                s.partidos
                    .filter { p ->
                        s.search.isBlank() ||
                                p.rival.contains(s.search, ignoreCase = true) ||
                                p.lugar.contains(s.search, ignoreCase = true)
                    }
                    .filter { p ->
                        when (s.filtro) {
                            PartidoFiltro.TODOS       -> true
                            PartidoFiltro.PROXIMOS    -> p.resultadoFinal == null && p.isActive
                            PartidoFiltro.FINALIZADOS -> p.resultadoFinal != null
                            PartidoFiltro.ACTIVOS     -> p.isActive
                            PartidoFiltro.INACTIVOS   -> !p.isActive
                        }
                    }
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        init { load() }

        fun load() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                repository.getPartidos()
                    .onSuccess { lista ->
                        _state.update { it.copy(partidos = lista, isLoading = false) }
                    }
                    .onFailure { e ->
                        _state.update { it.copy(isLoading = false, error = e.message) }
                    }
            }
        }

        fun setSearch(query: String) = _state.update { it.copy(search = query) }
        fun setFiltro(filtro: PartidoFiltro) = _state.update { it.copy(filtro = filtro) }

        fun createPartido(payload: PartidoPayload) {
            _formState.value = PartidoFormState.Saving
            viewModelScope.launch {
                repository.createPartido(payload)
                    .onSuccess { creado ->
                        _state.update { s ->
                            s.copy(partidos = listOf(creado) + s.partidos)
                        }
                        _formState.value = PartidoFormState.Success("Partido creado")
                    }
                    .onFailure { e ->
                        _formState.value = PartidoFormState.Error(e.message ?: "Error al crear")
                    }
            }
        }

        fun updatePartido(id: Int, payload: PartidoPayload) {
            _formState.value = PartidoFormState.Saving
            viewModelScope.launch {
                repository.updatePartido(id, payload)
                    .onSuccess { actualizado ->
                        _state.update { s ->
                            s.copy(partidos = s.partidos.map { if (it.id == id) actualizado else it })
                        }
                        _formState.value = PartidoFormState.Success("Partido actualizado")
                    }
                    .onFailure { e ->
                        _formState.value = PartidoFormState.Error(e.message ?: "Error al actualizar")
                    }
            }
        }

        fun actualizarResultado(id: Int, resultado: String, onResult: (String) -> Unit) {
            viewModelScope.launch {
                repository.actualizarResultado(id, resultado)
                    .onSuccess { actualizado ->
                        _state.update { s ->
                            s.copy(partidos = s.partidos.map { if (it.id == id) actualizado else it })
                        }
                        onResult("Resultado guardado: $resultado")
                    }
                    .onFailure { e ->
                        onResult("Error: ${e.message}")
                    }
            }
        }

        // Toggle optimista del campo is_active
        fun toggleActivo(id: Int, isActive: Boolean) {
            _state.update { s ->
                s.copy(partidos = s.partidos.map {
                    if (it.id == id) it.copy(isActive = isActive) else it
                })
            }
            viewModelScope.launch {
                val partido = _state.value.partidos.firstOrNull { it.id == id } ?: return@launch
                repository.updatePartido(
                    id,
                    PartidoPayload(
                        rival    = partido.rival,
                        fecha    = partido.fecha,
                        lugar    = partido.lugar,
                        isActive = isActive,
                    )
                ).onFailure {
                    // Revertir si falla
                    _state.update { s ->
                        s.copy(partidos = s.partidos.map { p ->
                            if (p.id == id) p.copy(isActive = !isActive) else p
                        })
                    }
                }
            }
        }

        fun deletePartido(id: Int) {
            viewModelScope.launch {
                repository.deletePartido(id)
                    .onSuccess {
                        _state.update { s ->
                            s.copy(partidos = s.partidos.filter { it.id != id })
                        }
                    }
                    .onFailure { e ->
                        _state.update { it.copy(error = e.message) }
                    }
            }
        }

        fun resetFormState() { _formState.value = PartidoFormState.Idle }
    }