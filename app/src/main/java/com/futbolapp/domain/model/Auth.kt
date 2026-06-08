package com.futbolapp.domain.model

data class AuthTokens(
    val access: String,
    val refresh: String,
)

data class LoggedUser(
    val id: Int,
    val username: String,
    val email: String,
    val tipoUsuario: String,
    val idioma: String,
)

data class RegisterPayload(
    val username: String,
    val email: String,
    val password: String,
    val password2: String,
    val tipoUsuario: String,   // "Coach", "Jugador", etc.
    val idioma: String,        // "Español", "Inglés", etc.
)