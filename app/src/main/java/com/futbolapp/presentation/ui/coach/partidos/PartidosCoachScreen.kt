// presentation/ui/coach/partidos/PartidosCoachScreen.kt
package com.futbolapp.presentation.ui.coach.partidos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.futbolapp.domain.model.Partido
import com.futbolapp.presentation.components.ErrorScreen
import com.futbolapp.presentation.components.LoadingScreen
import com.futbolapp.presentation.viewmodel.PartidoFiltro
import com.futbolapp.presentation.viewmodel.PartidosCoachViewModel
import com.futbolapp.theme.*

@Composable
fun PartidosCoachScreen(
    viewModel: PartidosCoachViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    val state     by viewModel.state.collectAsState()
    val filtered  by viewModel.filtered.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var showForm      by remember { mutableStateOf(false) }
    var editTarget    by remember { mutableStateOf<Partido?>(null) }
    var resultadoTarget by remember { mutableStateOf<Partido?>(null) }
    var deleteTarget  by remember { mutableStateOf<Partido?>(null) }
    var snackMsg      by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackMsg) {
        snackMsg?.let {
            snackbarHostState.showSnackbar(it)
            snackMsg = null
        }
    }

    Scaffold(
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        containerColor = Background,
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
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
                                "Gestión de partidos",
                                style      = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color      = TextPrimary,
                            )
                            Text(
                                "${state.partidos.size} partidos",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = viewModel::load) {
                                Icon(Icons.Default.Refresh, null, tint = TextSecondary)
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
                                Spacer(Modifier.width(4.dp))
                                Text("Nuevo", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value         = state.search,
                        onValueChange = viewModel::setSearch,
                        placeholder   = { Text("Buscar rival o lugar...", color = TextFaint) },
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

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(PartidoFiltro.entries) { filtro ->
                            FilterChip(
                                selected = state.filtro == filtro,
                                onClick  = { viewModel.setFiltro(filtro) },
                                label    = {
                                    Text(filtro.label, style = MaterialTheme.typography.labelSmall)
                                },
                                colors = FilterChipDefaults.filterChipColors(
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
                state.isLoading -> LoadingScreen("Cargando partidos...")
                state.error != null -> ErrorScreen(state.error!!, onRetry = viewModel::load)
                filtered.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⚽", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                if (state.search.isBlank() && state.filtro == PartidoFiltro.TODOS)
                                    "Sin partidos registrados"
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
                        items(filtered, key = { it.id }) { partido ->
                            PartidoCoachCard(
                                partido          = partido,
                                onPartidoClick = { onNavigateToDetail(partido.id) },
                                onToggle         = { viewModel.toggleActivo(partido.id, !partido.isActive) },
                                onEdit           = { editTarget = partido; showForm = true },
                                onResultado      = { resultadoTarget = partido },
                                onDelete         = { deleteTarget = partido },
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Bottom Sheet: formulario ──────────────────────────────────
    if (showForm) {
        PartidoFormSheet(
            initial   = editTarget,
            formState = formState,
            onSave    = { payload ->
                if (editTarget != null) viewModel.updatePartido(editTarget!!.id, payload)
                else viewModel.createPartido(payload)
            },
            onDismiss = {
                showForm   = false
                editTarget = null
                viewModel.resetFormState()
            },
        )
    }

    // ── Dialog: resultado ─────────────────────────────────────────
    resultadoTarget?.let { partido ->
        ResultadoDialog(
            partido      = partido,
            onActualizar = { resultado ->
                viewModel.actualizarResultado(partido.id, resultado) { msg ->
                    snackMsg       = msg
                    resultadoTarget = null
                }
            },
            onDismiss = { resultadoTarget = null },
        )
    }

    // ── Dialog: eliminar ──────────────────────────────────────────
    deleteTarget?.let { partido ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor   = Surface,
            shape            = MaterialTheme.shapes.large,
            title = { Text("¿Eliminar partido?", color = TextPrimary) },
            text  = {
                Text(
                    "\"vs ${partido.rival}\" del ${partido.fecha.take(10)} se eliminará permanentemente.",
                    color = TextSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePartido(partido.id)
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

// ── Card de partido con acciones ──────────────────────────────

@Composable
private fun PartidoCoachCard(
    partido:     Partido,
    onPartidoClick: () -> Unit,
    onToggle:    () -> Unit,
    onEdit:      () -> Unit,
    onResultado: () -> Unit,
    onDelete:    () -> Unit,
) {
    val esFinalizado = partido.resultadoFinal != null

    Surface(
        onClick  = onPartidoClick,
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
            // Toggle activo
            Switch(
                checked         = partido.isActive,
                onCheckedChange = { onToggle() },
                colors          = SwitchDefaults.colors(
                    checkedThumbColor    = AccentOnDark,
                    checkedTrackColor    = Accent,
                    uncheckedTrackColor  = Surface2,
                    uncheckedBorderColor = Border,
                ),
            )

            Spacer(Modifier.width(10.dp))

            // Icono de estado
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .background(
                        color = if (esFinalizado) Surface2 else Accent.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.medium,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(if (esFinalizado) "✓" else "⚽", fontSize = 20.sp)
            }

            Spacer(Modifier.width(10.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "vs ${partido.rival}",
                    style      = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary,
                    maxLines   = 1,
                )
                Text(
                    partido.lugar,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                Text(
                    text  = partido.fecha.take(10),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (esFinalizado) TextFaint else Accent,
                )
            }

            // Resultado o badge
            Column(horizontalAlignment = Alignment.End) {
                if (partido.resultadoFinal != null) {
                    Surface(shape = MaterialTheme.shapes.small, color = Surface2) {
                        Text(
                            text       = partido.resultadoFinal,
                            style      = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                            modifier   = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                } else {
                    Surface(shape = MaterialTheme.shapes.small, color = StatusProgramado.copy(alpha = 0.15f)) {
                        Text(
                            "Pendiente",
                            style    = MaterialTheme.typography.labelSmall,
                            color    = StatusProgramado,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row {
                    // Botón marcador
                    IconButton(onClick = onResultado, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.SportsSoccer, null,
                            tint     = Accent,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Edit, null,
                            tint     = TextSecondary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Delete, null,
                            tint     = Error,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}