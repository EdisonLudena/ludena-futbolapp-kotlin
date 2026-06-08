// presentation/ui/evaluaciones/EvaluacionesScreen.kt
package com.futbolapp.presentation.ui.evaluaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
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
import com.futbolapp.presentation.viewmodel.EvaluacionViewModel
import com.futbolapp.theme.*

@Composable
fun EvaluacionesScreen(
    onEvaluacionClick: (Int) -> Unit,
    onCrearClick:      () -> Unit,
    isCoach:           Boolean = false,
    viewModel:         EvaluacionViewModel = hiltViewModel(),
) {
    val state by viewModel.listState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // ── Cabecera ──────────────────────────────────────────
        Surface(color = Surface, tonalElevation = 0.dp) {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text       = "Evaluaciones",
                        style      = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                    )
                    Text(
                        text  = "${state.evaluaciones.size} registradas",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = viewModel::loadEvaluaciones) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = TextSecondary)
                    }
                    if (isCoach) {
                        IconButton(onClick = onCrearClick) {
                            Icon(Icons.Default.Add, contentDescription = "Nueva evaluación", tint = Accent)
                        }
                    }
                }
            }
        }

        // ── Contenido ─────────────────────────────────────────
        when {
            state.isLoading -> LoadingScreen("Cargando evaluaciones...")
            state.error != null -> ErrorScreen(
                message = state.error!!,
                onRetry = viewModel::loadEvaluaciones,
            )
            state.evaluaciones.isEmpty() -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📊", style = MaterialTheme.typography.displayMedium)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Sin evaluaciones",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                        )
                        Text(
                            "Las evaluaciones físicas y técnicas\naparecerán aquí",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                        if (isCoach) {
                            Spacer(Modifier.height(20.dp))
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
            }
            else -> {
                LazyColumn(
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier            = Modifier.fillMaxSize(),
                ) {
                    items(state.evaluaciones, key = { it.id }) { ev ->
                        EvaluacionCard(
                            evaluacion = ev,
                            onClick    = { onEvaluacionClick(ev.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EvaluacionCard(evaluacion: Evaluacion, onClick: () -> Unit) {
    Surface(
        onClick        = onClick,
        shape          = MaterialTheme.shapes.large,
        color          = Surface,
        tonalElevation = 0.dp,
        modifier       = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header: jugador + fecha
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = "Jugador #${evaluacion.jugadorId}",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                    )
                    Text(
                        text  = evaluacion.fechaRegistro.take(10),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
                CalificacionBadge(nota = evaluacion.calificacionTecnica)
            }

            Spacer(Modifier.height(12.dp))

            // Métricas en chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier              = Modifier.fillMaxWidth(),
            ) {
                MetricaChip(label = "Peso", valor = "${evaluacion.pesoKg} kg")
                MetricaChip(label = "Altura", valor = "${evaluacion.alturaCm} cm")
                MetricaChip(label = "Vel.", valor = "${evaluacion.velocidadSeg}s")
            }

            // Nota si existe
            if (evaluacion.notasComentario.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text     = evaluacion.notasComentario,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = TextSecondary,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
private fun MetricaChip(label: String, valor: String) {
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = Surface2,
    ) {
        Column(
            modifier            = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(valor, style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(label, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = TextSecondary)
        }
    }
}