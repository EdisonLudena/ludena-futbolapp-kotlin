// data/remote/dto/AuthDto.kt
package com.futbolapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val username: String,
    val password: String,
)

data class RegisterRequest(
    val username:    String,
    val email:       String,
    val password:    String,
    @SerializedName("password2")    val password2:   String,
    @SerializedName("tipo_usuario") val tipoUsuario: String,
    val idioma:      String,
)

data class TokenRefreshRequest(
    val refresh: String,
)

data class AuthResponseDto(
    val access:  String,
    val refresh: String,
    // Ajusta estos campos según lo que devuelva tu endpoint /auth/login/
    @SerializedName("user_id")      val userId:      Int?    = null,
    val username:                                    String? = null,
    val email:                                       String? = null,
    @SerializedName("tipo_usuario") val tipoUsuario: String? = null,
    val idioma:                                      String? = null,
)

data class TokenRefreshResponseDto(
    val access:  String,
    val refresh: String?,   // con ROTATE_REFRESH_TOKENS=True también devuelve nuevo refresh
)