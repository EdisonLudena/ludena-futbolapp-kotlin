// domain/repository/AuthRepository.kt
package com.futbolapp.domain.repository

import com.futbolapp.data.local.TokenDataStore
import com.futbolapp.domain.model.LoggedUser

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoggedUser>
    suspend fun register(
        username:    String,
        email:       String,
        password:    String,
        password2:   String,
        tipoUsuario: String,
        idioma:      String,
    ): Result<LoggedUser>
    suspend fun logout(): Result<Unit>
    suspend fun getStoredUser(): TokenDataStore.UserSnapshot?
    suspend fun isLoggedIn(): Boolean
}