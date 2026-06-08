// presentation/ui/auth/RegisterScreen.kt
package com.futbolapp.presentation.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.futbolapp.presentation.components.*
import com.futbolapp.presentation.viewmodel.AuthViewModel
import com.futbolapp.theme.*

// Opciones que define tu backend
private val TIPOS_USUARIO = listOf("Coach", "Scout")
private val IDIOMAS       = listOf("Español", "Inglés", "Portugués", "Francés")

@Composable
fun RegisterScreen(
    onRegisterSuccess: (isCoach: Boolean) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var username    by remember { mutableStateOf("") }
    var email       by remember { mutableStateOf("") }
    var password    by remember { mutableStateOf("") }
    var password2   by remember { mutableStateOf("") }
    var tipoUsuario by remember { mutableStateOf(TIPOS_USUARIO.first()) }
    var idioma      by remember { mutableStateOf(IDIOMAS.first()) }

    var tipoExpanded   by remember { mutableStateOf(false) }
    var idiomaExpanded by remember { mutableStateOf(false) }

    val passwordMismatch = password.isNotEmpty() &&
            password2.isNotEmpty() &&
            password != password2

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            val user = (uiState as AuthUiState.Success).user
            onRegisterSuccess(user.tipoUsuario.equals("Coach", ignoreCase = true))
        }
    }

    val isLoading = uiState is AuthUiState.Loading
    val errorMsg  = (uiState as? AuthUiState.Error)?.message

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text       = "⚽ FutbolApp",
                fontSize   = 36.sp,
                fontWeight = FontWeight.Bold,
                color      = Accent,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = "Crea tu cuenta",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(32.dp))

            Surface(
                shape          = MaterialTheme.shapes.large,
                color          = Surface,
                tonalElevation = 0.dp,
                modifier       = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    if (errorMsg != null) {
                        Surface(
                            color    = Error.copy(alpha = 0.1f),
                            shape    = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text     = errorMsg,
                                color    = Error,
                                style    = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    FutbolTextField(
                        value         = username,
                        onValueChange = { username = it; viewModel.clearError() },
                        label         = "Usuario",
                        placeholder   = "mínimo 3 caracteres",
                        enabled       = !isLoading,
                        isError       = username.isNotEmpty() && username.length < 3,
                        errorMessage  = "Mínimo 3 caracteres",
                        imeAction     = ImeAction.Next,
                    )
                    Spacer(Modifier.height(14.dp))

                    FutbolTextField(
                        value         = email,
                        onValueChange = { email = it; viewModel.clearError() },
                        label         = "Email",
                        placeholder   = "tu@email.com",
                        enabled       = !isLoading,
                        keyboardType  = KeyboardType.Email,
                        isError       = email.isNotEmpty() && !email.contains("@"),
                        errorMessage  = "Email inválido",
                        imeAction     = ImeAction.Next,
                    )
                    Spacer(Modifier.height(14.dp))

                    FutbolTextField(
                        value         = password,
                        onValueChange = { password = it; viewModel.clearError() },
                        label         = "Contraseña",
                        placeholder   = "mínimo 8 caracteres",
                        isPassword    = true,
                        enabled       = !isLoading,
                        keyboardType  = KeyboardType.Password,
                        isError       = password.isNotEmpty() && password.length < 8,
                        errorMessage  = "Mínimo 8 caracteres",
                        imeAction     = ImeAction.Next,
                    )
                    Spacer(Modifier.height(14.dp))

                    FutbolTextField(
                        value         = password2,
                        onValueChange = { password2 = it; viewModel.clearError() },
                        label         = "Confirmar contraseña",
                        placeholder   = "repite la contraseña",
                        isPassword    = true,
                        enabled       = !isLoading,
                        keyboardType  = KeyboardType.Password,
                        isError       = passwordMismatch,
                        errorMessage  = "Las contraseñas no coinciden",
                        imeAction     = ImeAction.Done,
                    )
                    Spacer(Modifier.height(14.dp))

                    // ── Tipo de usuario ────────────────────────────────
                    Text(
                        text  = "Tipo de usuario",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                    Spacer(Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded        = tipoExpanded,
                        onExpandedChange = { tipoExpanded = it },
                    ) {
                        OutlinedTextField(
                            value         = tipoUsuario,
                            onValueChange = {},
                            readOnly      = true,
                            modifier      = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(tipoExpanded) },
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = Accent,
                                unfocusedBorderColor = Border,
                            ),
                        )
                        ExposedDropdownMenu(
                            expanded        = tipoExpanded,
                            onDismissRequest = { tipoExpanded = false },
                        ) {
                            TIPOS_USUARIO.forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(text = tipo) },
                                    onClick = { tipoUsuario = tipo; tipoExpanded = false },
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))

                    // ── Idioma ─────────────────────────────────────────
                    Text(
                        text  = "Idioma",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                    Spacer(Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded         = idiomaExpanded,
                        onExpandedChange = { idiomaExpanded = it },
                    ) {
                        OutlinedTextField(
                            value         = idioma,
                            onValueChange = {},
                            readOnly      = true,
                            modifier      = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(idiomaExpanded) },
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = Accent,
                                unfocusedBorderColor = Border,
                            ),
                        )
                        ExposedDropdownMenu(
                            expanded         = idiomaExpanded,
                            onDismissRequest = { idiomaExpanded = false },
                        ) {
                            IDIOMAS.forEach { lang ->
                                DropdownMenuItem(
                                    text    = { Text(lang) },
                                    onClick = { idioma = lang; idiomaExpanded = false },
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))

                    val canSubmit = username.length >= 3 &&
                            email.contains("@") &&
                            password.length >= 8 &&
                            !passwordMismatch &&
                            !isLoading

                    FutbolButton(
                        text      = "Crear mi cuenta",
                        onClick   = {
                            // Mapeo aquí: "Jugador" (que viene de la UI) -> "Player" (que espera el backend)
                            val valorParaBackend = when(tipoUsuario) {
                                "Coach" -> "Coach"
                                "Scout" -> "Scout"
                                else    -> "Coach"
                            }

                            viewModel.register(
                                username,
                                email,
                                password,
                                password2,
                                valorParaBackend, // <--- Enviamos el valor correcto
                                idioma
                            )
                        },
                        isLoading = isLoading,
                        enabled   = canSubmit,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text  = "¿Ya tienes cuenta? ",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text       = "Inicia sesión",
                        color      = Accent,
                        style      = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}