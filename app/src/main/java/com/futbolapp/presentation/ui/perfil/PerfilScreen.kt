// presentation/ui/perfil/PerfilScreen.kt
package com.futbolapp.presentation.ui.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.futbolapp.presentation.viewmodel.AuthViewModel
import com.futbolapp.theme.*

@Composable
fun PerfilScreen(
    authViewModel: AuthViewModel,
    onLogout:      () -> Unit,
) {
    val user by authViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Usamos el color del tema
            .verticalScroll(rememberScrollState())
            .padding(16.dp), // Padding global ajustado
    ) {
        // ── Avatar y nombre ───────────────────────────────────
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier         = Modifier
                    .size(88.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary),
                        ),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = user?.username?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    fontSize   = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text       = user?.username ?: "—",
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text  = user?.email ?: "—",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))

            // Chip de tipo_usuario - Estilo Premium
            user?.tipoUsuario?.let { tipo ->
                Surface(
                    color  = MaterialTheme.colorScheme.primaryContainer, // AccentSubtle equivalente
                    shape  = RoundedCornerShape(99.dp), // ShapeChip premium
                ) {
                    Text(
                        text          = tipo.uppercase(),
                        color         = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize      = 12.sp,
                        fontWeight    = FontWeight.Bold,
                        modifier      = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        letterSpacing = 0.5.sp,
                    )
                }
            }
        }

        // ── Info del usuario ──────────────────────────────────
        Surface(
            color          = MaterialTheme.colorScheme.surface,
            shape          = MaterialTheme.shapes.large,
            tonalElevation = 2.dp, // Elevación para profundidad
            modifier       = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(20.dp)) { // Padding generoso
                Text(
                    text          = "INFORMACIÓN DE LA CUENTA",
                    style         = MaterialTheme.typography.labelSmall,
                    color         = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.0.sp,
                    modifier      = Modifier.padding(bottom = 16.dp),
                )

                val filas = listOfNotNull(
                    "ID"          to (user?.id?.toString() ?: "—"),
                    "Usuario"     to (user?.username ?: "—"),
                    "Email"       to (user?.email ?: "—"),
                    "Tipo"        to (user?.tipoUsuario ?: "—"),
                    "Idioma"      to (user?.idioma ?: "—"),
                )

                filas.forEachIndexed { i, (label, value) ->
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    if (i < filas.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp) // Divisor sutil
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Cerrar sesión ─────────────────────────────────────
        var showConfirm by remember { mutableStateOf(false) }

        Button(
            onClick  = { showConfirm = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp), // Altura premium
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor   = MaterialTheme.colorScheme.onErrorContainer
            ),
            shape  = MaterialTheme.shapes.medium,
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión", style = MaterialTheme.typography.labelLarge)
        }

        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                containerColor   = Surface,
                title            = { Text("¿Cerrar sesión?", color = TextPrimary) },
                text             = { Text("Tu sesión se cerrará en este dispositivo.", color = TextSecondary) },
                confirmButton    = {
                    TextButton(onClick = {
                        showConfirm = false
                        authViewModel.logout()
                        onLogout()
                    }) {
                        Text("Cerrar sesión", color = Error, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton    = {
                    TextButton(onClick = { showConfirm = false }) {
                        Text("Cancelar", color = TextSecondary)
                    }
                },
                shape = MaterialTheme.shapes.large,
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}