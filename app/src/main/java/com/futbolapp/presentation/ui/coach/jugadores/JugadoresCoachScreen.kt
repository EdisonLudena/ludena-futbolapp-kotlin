// presentation/ui/coach/jugadores/JugadoresCoachScreen.kt
package com.futbolapp.presentation.ui.coach.jugadores

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.futbolapp.domain.model.Jugador
import com.futbolapp.presentation.components.ErrorScreen
import com.futbolapp.presentation.components.LoadingScreen
import com.futbolapp.presentation.viewmodel.JugadoresCoachViewModel
import com.futbolapp.theme.*

@Composable
fun JugadoresCoachScreen(
    onJugadorClick: (Int) -> Unit,
    viewModel: JugadoresCoachViewModel = hiltViewModel(),
) {
    val state     by viewModel.state.collectAsState()
    val filtered  by viewModel.filtered.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var showForm     by remember { mutableStateOf(false) }
    var editTarget   by remember { mutableStateOf<Jugador?>(null) }
    var deleteTarget by remember { mutableStateOf<Jugador?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // ── Cabecera ──────────────────────────────────────────
        Surface(color = Surface, tonalElevation = 0.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text       = "Gestión de jugadores",
                            style      = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                        )
                        Text(
                            text  = "${state.jugadores.size} jugadores",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                    Button(
                        onClick = { editTarget = null; showForm = true },
                        colors  = ButtonDefaults.buttonColors(
                            containerColor = Accent,
                            contentColor   = AccentOnDark,
                        ),
                        shape          = MaterialTheme.shapes.medium,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Nuevo", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value         = state.search,
                    onValueChange = viewModel::setSearch,
                    placeholder   = { Text("Buscar jugador...", color = TextFaint) },
                    leadingIcon   = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = MaterialTheme.shapes.medium,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Accent,
                        unfocusedBorderColor = Border,
                        cursorColor          = Accent,
                    ),
                )
            }
        }

        // ── Contenido ─────────────────────────────────────────
        when {
            state.isLoading -> LoadingScreen("Cargando jugadores...")
            state.error != null -> ErrorScreen(state.error!!, onRetry = viewModel::load)
            filtered.isEmpty() -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👥", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (state.search.isBlank()) "Sin jugadores registrados"
                            else "Sin resultados",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(filtered, key = { it.id }) { jugador ->
                        JugadorCoachCard(
                            jugador  = jugador,
                            onToggle = { viewModel.toggleActivo(jugador.id, !jugador.isActive) },
                            onEdit   = { editTarget = jugador; showForm = true },
                            onDelete = { deleteTarget = jugador },
                            onClick  = { onJugadorClick(jugador.id) },
                        )
                    }
                }
            }
        }
    }

    // ── Bottom Sheet del formulario ───────────────────────────
    if (showForm) {
        JugadorFormSheet(
            initial   = editTarget,
            formState = formState,
            onSave    = { payload ->
                if (editTarget != null) viewModel.updateJugador(editTarget!!.id, payload)
                else viewModel.createJugador(payload)
            },
            onDismiss = {
                showForm   = false
                editTarget = null
                viewModel.resetFormState()
            },
        )
    }

    // ── Diálogo de confirmación de eliminación ────────────────
    deleteTarget?.let { jugador ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor   = Surface,
            title = {
                Text("¿Eliminar jugador?", color = TextPrimary)
            },
            text = {
                Text(
                    "\"${jugador.nombres} ${jugador.apellidos}\" se eliminará permanentemente.",
                    color = TextSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteJugador(jugador.id)
                    deleteTarget = null
                }) {
                    Text("Eliminar", color = Error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            shape = MaterialTheme.shapes.large,
        )
    }
}

// ── Card de jugador con acciones ──────────────────────────────

@Composable
private fun JugadorCoachCard(
    jugador:  Jugador,
    onToggle: () -> Unit,
    onEdit:   () -> Unit,
    onDelete: () -> Unit,
    onClick:  () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape    = MaterialTheme.shapes.large,
        color    = Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Toggle activo/inactivo
            Switch(
                checked         = jugador.isActive,
                onCheckedChange = { onToggle() },
                colors          = SwitchDefaults.colors(
                    checkedThumbColor    = AccentOnDark,
                    checkedTrackColor    = Accent,
                    uncheckedTrackColor  = Surface2,
                    uncheckedBorderColor = Border,
                ),
            )

            Spacer(Modifier.width(10.dp))

            // Avatar
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Surface2),
                contentAlignment = Alignment.Center,
            ) {
                if (jugador.fotoUrl != null) {
                    AsyncImage(
                        model              = jugador.fotoUrl,
                        contentDescription = jugador.nombres,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize(),
                    )
                } else {
                    Text(
                        text       = jugador.nombres.take(1).uppercase(),
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Accent,
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text       = "${jugador.nombres} ${jugador.apellidos}",
                        style      = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextPrimary,
                        maxLines   = 1,
                    )
                    if (!jugador.isActive) {
                        Surface(
                            color = Error.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.extraSmall,
                        ) {
                            Text(
                                "Inactivo",
                                color      = Error,
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text  = jugador.posicion,
                        style = MaterialTheme.typography.bodySmall,
                        color = Accent,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text("·", style = MaterialTheme.typography.bodySmall, color = TextFaint)
                    Text(
                        text  = jugador.categoria,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }

            // Acciones
            Row {
                IconButton(
                    onClick = { onEdit() },
                    modifier = Modifier.clickable(onClick = { onEdit() }, indication = null, interactionSource = remember { MutableInteractionSource() })
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TextSecondary)
                }
                IconButton(
                    onClick = { onDelete() },
                    modifier = Modifier.clickable(onClick = { onDelete() }, indication = null, interactionSource = remember { MutableInteractionSource() })
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Error)
                }
            }
        }
    }
}