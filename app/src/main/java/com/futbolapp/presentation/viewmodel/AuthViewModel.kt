// presentation/viewmodel/AuthViewModel.kt
package com.futbolapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futbolapp.data.local.TokenDataStore
import com.futbolapp.domain.model.LoggedUser
import com.futbolapp.domain.repository.AuthRepository
import com.futbolapp.presentation.ui.auth.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenDataStore: TokenDataStore,
) : ViewModel() {

    // ── Estado de la UI ───────────────────────────────────────
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ── Usuario logueado (Flow reactivo) ──────────────────────
    private val _currentUser = MutableStateFlow<LoggedUser?>(null)
    val currentUser: StateFlow<LoggedUser?> = _currentUser.asStateFlow()

    val isAuthenticated: StateFlow<Boolean> = _currentUser
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Coach es el rol con acceso a gestión del equipo
    val isCoach: StateFlow<Boolean> = _currentUser
        .map { it?.tipoUsuario.equals("Coach", ignoreCase = true) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // ── Estado de carga inicial ───────────────────────────────
    private val _isCheckingSession = MutableStateFlow(true)
    val isCheckingSession: StateFlow<Boolean> = _isCheckingSession.asStateFlow()

    init {
        restoreSession()
    }

    // Restaurar sesión desde DataStore al arrancar la app
    private fun restoreSession() {
        viewModelScope.launch {
            try {
                val snapshot = authRepository.getStoredUser()
                if (snapshot != null && authRepository.isLoggedIn()) {
                    _currentUser.value = LoggedUser(
                        id          = snapshot.id,
                        username    = snapshot.username,
                        email       = snapshot.email,
                        tipoUsuario = snapshot.tipoUsuario,
                        idioma      = snapshot.idioma,
                    )
                }
            } finally {
                _isCheckingSession.value = false
            }
        }
    }

    // ── Login ─────────────────────────────────────────────────
    fun login(username: String, password: String) {
        if (_uiState.value is AuthUiState.Loading) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.login(username.trim(), password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _uiState.value     = AuthUiState.Success(user)
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Error al iniciar sesión")
                }
        }
    }

    // ── Registro ──────────────────────────────────────────────
    fun register(
        username:    String,
        email:       String,
        password:    String,
        password2:   String,
        tipoUsuario: String,
        idioma:      String,
    ) {
        if (_uiState.value is AuthUiState.Loading) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.register(
                username.trim(), email.trim(), password, password2, tipoUsuario, idioma
            )
                .onSuccess { user ->
                    _currentUser.value = user
                    _uiState.value     = AuthUiState.Success(user)
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Error al registrarse")
                }
        }
    }

    // ── Logout ────────────────────────────────────────────────
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _currentUser.value = null
            _uiState.value     = AuthUiState.Idle
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}