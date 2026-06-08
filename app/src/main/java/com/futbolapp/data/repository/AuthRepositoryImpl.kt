// data/repository/AuthRepositoryImpl.kt
package com.futbolapp.data.repository

import com.futbolapp.data.local.TokenDataStore
import com.futbolapp.data.remote.api.AuthApi
import com.futbolapp.data.remote.dto.*
import com.futbolapp.domain.model.LoggedUser
import com.futbolapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api:            AuthApi,
    private val tokenDataStore: TokenDataStore,
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<LoggedUser> =
        runCatching {
            val response = api.login(LoginRequest(username, password))
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string() ?: ""
                error(parseErrorMessage(errorBody, response.code()))
            }
            val body = response.body()!!
            tokenDataStore.saveTokens(body.access, body.refresh)
            // Guarda lo que el backend devuelva; ajusta si tu /auth/login/
            // no retorna todos estos campos todavía
            tokenDataStore.saveUser(
                id          = body.userId ?: 0,
                username    = body.username ?: username,
                email       = body.email ?: "",
                tipoUsuario = body.tipoUsuario ?: "",
                idioma      = body.idioma ?: "",
            )
            LoggedUser(
                id          = body.userId ?: 0,
                username    = body.username ?: username,
                email       = body.email ?: "",
                tipoUsuario = body.tipoUsuario ?: "",
                idioma      = body.idioma ?: "",
            )
        }

    override suspend fun register(
        username:    String,
        email:       String,
        password:    String,
        password2:   String,
        tipoUsuario: String,
        idioma:      String,
    ): Result<LoggedUser> = runCatching {
        val response = api.register(
            RegisterRequest(username, email, password, password2, tipoUsuario, idioma)
        )
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: ""
            error(parseErrorMessage(errorBody, response.code()))
        }
        val body = response.body()!!
        tokenDataStore.saveTokens(body.access, body.refresh)
        tokenDataStore.saveUser(
            id          = body.userId ?: 0,
            username    = body.username ?: username,
            email       = body.email ?: email,
            tipoUsuario = body.tipoUsuario ?: tipoUsuario,
            idioma      = body.idioma ?: idioma,
        )
        LoggedUser(
            id          = body.userId ?: 0,
            username    = body.username ?: username,
            email       = body.email ?: email,
            tipoUsuario = body.tipoUsuario ?: tipoUsuario,
            idioma      = body.idioma ?: idioma,
        )
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        tokenDataStore.clearSession()
    }

    override suspend fun getStoredUser(): TokenDataStore.UserSnapshot? =
        tokenDataStore.userSnapshot.first()

    override suspend fun isLoggedIn(): Boolean =
        !tokenDataStore.getAccessToken().isNullOrBlank()

    // Extrae el mensaje de error legible del JSON de Django
    private fun parseErrorMessage(body: String, code: Int): String {
        return try {
            val map = com.google.gson.Gson().fromJson(body, Map::class.java)
            map["detail"]?.toString()
                ?: map["non_field_errors"]?.toString()
                ?: map.values.firstOrNull()?.toString()
                ?: "Error $code"
        } catch (e: Exception) {
            "Error $code"
        }
    }
}