// presentation/ui/coach/evaluaciones/EvaluacionesCoachScreen.kt
package com.futbolapp.presentation.ui.evaluaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.futbolapp.domain.model.Evaluacion
import com.futbolapp.domain.model.Jugador
import com.futbolapp.presentation.components.CalificacionBadge
import com.futbolapp.presentation.components.ErrorScreen
import com.futbolapp.presentation.components.LoadingScreen
import com.futbolapp.presentation.components.calificacionColor
import com.futbolapp.presentation.viewmodel.EvaluacionFiltro
import com.futbolapp.presentation.viewmodel.EvaluacionesCoachViewModel
import com.futbolapp.theme.*

@Composable
fun EvaluacionesCoachScreen(
    onCrearClick:      () -> Unit,
    onEditClick:       (Int) -> Unit,
    onEvaluacionClick: (Int) -> Unit,
    viewModel: EvaluacionesCoachViewModel = hiltViewModel(),
) {
    val state        by viewModel.state.collectAsState()
    val filtered     by viewModel.filtered.collectAsState()
    val jugadoresMap by viewModel.jugadoresMap.collectAsState()

    var deleteTarget by remember { mutableStateOf<Evaluacion?>(null) }

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
                            "Evaluaciones",
                            style      = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                        )
                        Text(
                            "${state.total} evaluaciones registradas",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = viewModel::load) {
                            Icon(Icons.Default.Refresh, null, tint = TextSecondary)
                        }
                        Button(
                            onClick        = onCrearClick,
                            colors         = ButtonDefaults.buttonColors(
                                containerColor = Accent,
                                contentColor   = AccentOnDark,
                            ),
                            shape          = MaterialTheme.shapes.medium,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Nueva", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Búsqueda por nombre de jugador
                OutlinedTextField(
                    value         = state.search,
                    onValueChange = viewModel::setSearch,
                    placeholder   = { Text("Buscar por nombre del jugador...", color = TextFaint) },
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

                Spacer(Modifier.height(10.dp))

                // Chips de filtro por calificación
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(EvaluacionFiltro.entries) { filtro ->
                        FilterChip(
                            selected = state.filtro == filtro,
                            onClick  = { viewModel.setFiltro(filtro) },
                            label    = {
                                Text(filtro.label, style = MaterialTheme.typography.labelSmall)
                            },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Accent,
                                selectedLabelColor     = AccentOnDark,
                                containerColor         = Surface2,
                                labelColor             = TextSecondary,
                            ),
                        )
                    }
                }
            }
        }

        // ── Contenido ─────────────────────────────────────────
        when {
            state.isLoading -> LoadingScreen("Cargando evaluaciones...")
            state.error != null -> ErrorScreen(state.error!!, onRetry = viewModel::load)
            filtered.isEmpty() -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📊", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (state.search.isBlank() && state.filtro == EvaluacionFiltro.TODOS)
                                "Sin evaluaciones registradas"
                            else "Sin resultados",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onCrearClick,
                            colors  = ButtonDefaults.buttonColors(
                                containerColor = Accent,
                                contentColor   = AccentOnDark,
                            ),
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Nueva evaluación")
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(filtered, key = { it.id }) { ev ->
                        EvaluacionCoachCard(
                            evaluacion   = ev,
                            jugador      = jugadoresMap[ev.jugadorId],
                            onClick      = { onEvaluacionClick(ev.id) },
                            onEdit       = { onEditClick(ev.id) },
                            onDelete     = { deleteTarget = ev },
                        )
                    }
                }
            }
        }
    }

    // ── Diálogo de eliminación ────────────────────────────────
    deleteTarget?.let { ev ->
        val jugador = jugadoresMap[ev.jugadorId]
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor   = Surface,
            shape            = MaterialTheme.shapes.large,
            title = { Text("¿Eliminar evaluación?", color = TextPrimary) },
            text  = {
                Text(
                    buildString {
                        append("Evaluación del ")
                        append(ev.fechaRegistro.take(10))
                        jugador?.let { append(" · ${it.nombres} ${it.apellidos}") }
                        append(" se eliminará permanentemente.")
                    },
                    color = TextSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteEvaluacion(ev.id)
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
        )
    }
}

// ── Card de evaluación con acciones ──────────────────────────

@Composable
private fun EvaluacionCoachCard(
    evaluacion: Evaluacion,
    jugador:    Jugador?,
    onClick:    () -> Unit,
    onEdit:     () -> Unit,
    onDelete:   () -> Unit,
) {
    Surface(
        onClick        = onClick,
        shape          = MaterialTheme.shapes.large,
        color          = Surface,
        tonalElevation = 0.dp,
        modifier       = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier          = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ... (Tu código del Avatar se mantiene igual) ...

            // Columna central de datos (la que ocupa el espacio restante)
            Column(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(
                    text       = jugador?.let { "${it.nombres} ${it.apellidos}" } ?: "Jugador #${evaluacion.jugadorId}",
                    style      = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary,
                    maxLines   = 1,
                )
                jugador?.let {
                    Text(text = "${it.posicion} · ${it.categoria}", style = MaterialTheme.typography.bodySmall, color = Accent)
                }
                Text(text = evaluacion.fechaRegistro.take(10), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(
                    text  = "${evaluacion.pesoKg}kg · ${evaluacion.alturaCm}cm · ${evaluacion.velocidadSeg}s",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextFaint,
                )
            }

            // --- ÚNICA COLUMNA DE ACCIONES ---
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                CalificacionBadge(nota = evaluacion.calificacionTecnica)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = Error, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}