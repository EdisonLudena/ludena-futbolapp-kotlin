// presentation/ui/evaluaciones/EvaluacionDetalleScreen.kt
package com.futbolapp.presentation.ui.evaluaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.futbolapp.domain.model.Evaluacion
import com.futbolapp.presentation.components.CalificacionBadge
import com.futbolapp.presentation.components.ErrorScreen
import com.futbolapp.presentation.components.LoadingScreen
import com.futbolapp.presentation.components.calificacionColor
import com.futbolapp.presentation.viewmodel.EvaluacionDetalleUiState
import com.futbolapp.presentation.viewmodel.EvaluacionViewModel
import com.futbolapp.theme.*

@Composable
fun EvaluacionDetalleScreen(
    evaluacionId: Int,
    onBack:       () -> Unit,
    isCoach:      Boolean = false,
    viewModel:    EvaluacionViewModel = hiltViewModel(),
) {
    val state by viewModel.detalleState.collectAsState()

    LaunchedEffect(evaluacionId) { viewModel.loadDetalle(evaluacionId) }

    when (val s = state) {
        is EvaluacionDetalleUiState.Loading ->
            LoadingScreen("Cargando evaluación...")
        is EvaluacionDetalleUiState.Error   ->
            ErrorScreen(s.message) { viewModel.loadDetalle(evaluacionId) }
        is EvaluacionDetalleUiState.Success ->
            EvaluacionDetalleContent(
                evaluacion = s.ev,
                isCoach    = isCoach,
                onBack     = onBack,
                onDelete   = { viewModel.deleteEvaluacion(s.ev.id, onBack) },
            )
    }
}

@Composable
private fun EvaluacionDetalleContent(
    evaluacion: Evaluacion,
    isCoach:    Boolean,
    onBack:     () -> Unit,
    onDelete:   () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor   = Surface,
            title = { Text("¿Eliminar evaluación?", color = TextPrimary) },
            text  = { Text("Esta acción es permanente.", color = TextSecondary) },
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
        // ── Header ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface2)
                .padding(horizontal = 16.dp, vertical = 20.dp),
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
            }
            Column(
                modifier            = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Evaluación #${evaluacion.id}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(evaluacion.fechaRegistro.take(10),
                    style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            if (isCoach) {
                IconButton(
                    onClick  = { showDeleteDialog = true },
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Error)
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // ── Calificación técnica (visual) ──────────────────
            SectionCard(title = "Calificación técnica") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val color = calificacionColor(evaluacion.calificacionTecnica)
                    Text(
                        text       = "${evaluacion.calificacionTecnica}",
                        fontSize   = 56.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = color,
                    )
                    CalificacionBadge(nota = evaluacion.calificacionTecnica)
                    Spacer(Modifier.height(12.dp))
                    // Barra de progreso visual
                    LinearProgressIndicator(
                        progress       = { evaluacion.calificacionTecnica / 100f },
                        modifier       = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color          = color,
                        trackColor     = Surface2,
                    )
                }
            }

            // ── Métricas físicas ──────────────────────────────
            SectionCard(title = "Métricas físicas") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricaRow("⚖️ Peso",       "${evaluacion.pesoKg} kg")
                    HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                    MetricaRow("📏 Altura",     "${evaluacion.alturaCm} cm")
                    HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                    MetricaRow("⚡ Velocidad",  "${evaluacion.velocidadSeg} seg (40m)")
                }
            }

            // ── Jugador ───────────────────────────────────────
            SectionCard(title = "Referencia") {
                MetricaRow("👤 Jugador ID", "#${evaluacion.jugadorId}")
            }

            // ── Notas ─────────────────────────────────────────
            if (evaluacion.notasComentario.isNotBlank()) {
                SectionCard(title = "Notas del entrenador") {
                    Text(
                        text  = evaluacion.notasComentario,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color    = Surface,
        shape    = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text          = title,
                style         = MaterialTheme.typography.labelSmall,
                color         = TextSecondary,
                letterSpacing = 0.8.sp,
                modifier      = Modifier.padding(bottom = 14.dp),
            )
            content()
        }
    }
}

@Composable
private fun MetricaRow(label: String, valor: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(
            valor,
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color      = TextPrimary,
        )
    }
}