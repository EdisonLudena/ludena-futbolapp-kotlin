// presentation/ui/jugadores/JugadorDetalleScreen.kt
package com.futbolapp.presentation.ui.jugadores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.futbolapp.presentation.components.FutbolButton
import com.futbolapp.presentation.components.LoadingScreen
import com.futbolapp.presentation.viewmodel.JugadorDetalleUiState
import com.futbolapp.presentation.viewmodel.JugadorDetalleViewModel
import com.futbolapp.theme.*

@Composable
fun JugadorDetalleScreen(
    jugadorId: Int,
    onBack:    () -> Unit,
    isCoach:   Boolean = false,
    viewModel: JugadorDetalleViewModel = hiltViewModel(),
) {
    val state      by viewModel.state.collectAsState()
    val isDeleting by viewModel.isDeleting.collectAsState()

    LaunchedEffect(jugadorId) { viewModel.load(jugadorId) }

    when (val s = state) {
        is JugadorDetalleUiState.Loading -> LoadingScreen("Cargando jugador...")
        is JugadorDetalleUiState.Error   -> ErrorScreen(s.message) { viewModel.load(jugadorId) }
        is JugadorDetalleUiState.Success -> JugadorDetalleContent(
            jugador        = s.jugador,
            isCoach        = isCoach,
            isDeleting     = isDeleting,
            onBack         = onBack,
            onToggleActivo = { viewModel.toggleActivo(s.jugador) {} },
            onDelete       = { viewModel.delete(s.jugador, onBack) },
        )
    }
}

@Composable
private fun JugadorDetalleContent(
    jugador:        Jugador,
    isCoach:        Boolean,
    isDeleting:     Boolean,
    onBack:         () -> Unit,
    onToggleActivo: () -> Unit,
    onDelete:       () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor   = Surface,
            title = { Text("¿Eliminar jugador?", color = TextPrimary) },
            text  = {
                Text(
                    "Esta acción eliminará permanentemente a ${jugador.nombres} ${jugador.apellidos}.",
                    color = TextSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete() }) {
                    Text("Eliminar", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState()),
    ) {
        // ── Header con avatar ──────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Surface2, Background),
                    ),
                )
                .padding(horizontal = 24.dp, vertical = 32.dp),
        ) {
            IconButton(
                onClick  = onBack,
                modifier = Modifier.align(Alignment.TopStart),
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
            }

            Column(
                modifier            = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(16.dp))

                Box(
                    modifier         = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Surface),
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
                            fontSize   = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Accent,
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text       = "${jugador.nombres} ${jugador.apellidos}",
                    style      = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                )

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipLabel(jugador.posicion, Accent.copy(alpha = 0.15f), AccentLight)
                    ChipLabel(jugador.categoria, Surface2, TextSecondary)
                    ChipLabel(
                        label = if (jugador.isActive) "Activo" else "Inactivo",
                        bg    = if (jugador.isActive) Success.copy(alpha = 0.15f) else Error.copy(alpha = 0.15f),
                        color = if (jugador.isActive) Success else Error,
                    )
                }
            }
        }

        // ── Info card ─────────────────────────────────────────
        Column(modifier = Modifier.padding(24.dp)) {
            InfoCard(
                items = listOf(
                    "Posición"  to jugador.posicion,
                    "Categoría" to jugador.categoria,
                    "Estado"    to if (jugador.isActive) "Activo" else "Inactivo",
                ),
            )

            // ── Acciones del Coach ─────────────────────────────
            if (isCoach) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text          = "Acciones",
                    style         = MaterialTheme.typography.labelSmall,
                    color         = TextSecondary,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick  = onToggleActivo,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Warning),
                    border   = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Warning)
                    ),
                    shape    = MaterialTheme.shapes.medium,
                ) {
                    Icon(
                        imageVector = if (jugador.isActive) Icons.Default.PersonOff
                        else Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier    = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (jugador.isActive) "Desactivar jugador" else "Reactivar jugador",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick  = { showDeleteDialog = true },
                    enabled  = !isDeleting,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                    border   = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Error)
                    ),
                    shape    = MaterialTheme.shapes.medium,
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(color = Error, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar jugador", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ChipLabel(
    label: String,
    bg:    androidx.compose.ui.graphics.Color,
    color: androidx.compose.ui.graphics.Color,
) {
    Surface(shape = MaterialTheme.shapes.small, color = bg) {
        Text(
            text     = label,
            style    = MaterialTheme.typography.labelMedium,
            color    = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun InfoCard(items: List<Pair<String, String>>) {
    Surface(
        shape          = MaterialTheme.shapes.large,
        color          = Surface,
        tonalElevation = 0.dp,
        modifier       = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            items.forEachIndexed { index, (label, value) ->
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text(
                        value,
                        style      = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextPrimary,
                    )
                }
                if (index < items.lastIndex) {
                    HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                }
            }
        }
    }
}