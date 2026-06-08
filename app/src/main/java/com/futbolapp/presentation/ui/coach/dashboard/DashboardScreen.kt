// presentation/ui/coach/dashboard/DashboardScreen.kt
package com.futbolapp.presentation.ui.coach.dashboard

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
import com.futbolapp.presentation.components.LoadingScreen
import com.futbolapp.presentation.viewmodel.DashboardStats
import com.futbolapp.presentation.viewmodel.DashboardUiState
import com.futbolapp.presentation.viewmodel.DashboardViewModel
import com.futbolapp.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel:  DashboardViewModel = hiltViewModel(),
) {
    val state       by viewModel.state.collectAsState()
    val lastUpdated by viewModel.lastUpdated.collectAsState()

    when (val s = state) {
        is DashboardUiState.Loading -> LoadingScreen("Cargando dashboard...")
        is DashboardUiState.Error   -> {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚠️ ${s.message}", color = Error)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = viewModel::load,
                        colors  = ButtonDefaults.buttonColors(containerColor = Accent),
                    ) { Text("Reintentar", color = AccentOnDark) }
                }
            }
        }
        is DashboardUiState.Success -> DashboardContent(
            stats       = s.stats,
            lastUpdated = lastUpdated,
            onNavigate  = onNavigate,
            onRefresh   = viewModel::load,
        )
    }
}

@Composable
private fun DashboardContent(
    stats:       DashboardStats,
    lastUpdated: Long,
    onNavigate:  (String) -> Unit,
    onRefresh:   () -> Unit,
) {
    val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val timeStr = if (lastUpdated > 0) timeFmt.format(Date(lastUpdated)) else "—"

    LazyColumn(
        modifier            = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // ── Header ────────────────────────────────────────────
        item {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text       = "Dashboard",
                        style      = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                    )
                    Text(
                        text  = "Actualizado: $timeStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextFaint,
                    )
                }
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Accent)
                }
            }
        }

        // ── KPIs fila 1: Jugadores ─────────────────────────────
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard(
                    title    = "Jugadores activos",
                    value    = stats.jugadoresActivos.toString(),
                    subtitle = "${stats.jugadoresInactivos} inactivos",
                    icon     = Icons.Default.People,
                    color    = Accent,
                    onClick  = { onNavigate("jugadores") },
                    modifier = Modifier.weight(1f),
                )
                KpiCard(
                    title    = "Próximos partidos",
                    value    = stats.proximosPartidos.toString(),
                    subtitle = "${stats.temporadaStats?.totalPartidos ?: 0} en total",
                    icon     = Icons.Default.SportsSoccer,
                    color    = Info,
                    onClick  = { onNavigate("partidos") },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // ── KPIs fila 2: Evaluaciones ──────────────────────────
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard(
                    title    = "Evaluaciones",
                    value    = stats.totalEvaluaciones.toString(),
                    subtitle = "registradas",
                    icon     = Icons.Default.Assessment,
                    color    = Success,
                    onClick  = { onNavigate("evaluaciones") },
                    modifier = Modifier.weight(1f),
                )
                KpiCard(
                    title    = "Promedio técnico",
                    value    = "${"%.0f".format(stats.promedioCalificacion)}/100",
                    subtitle = "calificación media",
                    icon     = Icons.Default.TrendingUp,
                    color    = Warning,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // ── Estadísticas de temporada ──────────────────────────
        stats.temporadaStats?.let { ts ->
            item {
                Surface(
                    color    = Surface,
                    shape    = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically,
                        ) {
                            Text(
                                text       = "Rendimiento de temporada",
                                style      = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color      = TextPrimary,
                            )
                            TextButton(onClick = { onNavigate("partidos") }) {
                                Text("Ver partidos", color = Accent,
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        // Barras de V/E/D
                        val total = ts.totalPartidos.coerceAtLeast(1).toFloat()
                        listOf(
                            Triple("Victorias", ts.victorias,  Success),
                            Triple("Empates",   ts.empates,    Warning),
                            Triple("Derrotas",  ts.derrotas,   Error),
                        ).forEach { (label, count, color) ->
                            val pct = (count.toFloat() / total).coerceIn(0.02f, 1f)
                            Column(modifier = Modifier.padding(bottom = 10.dp)) {
                                Row(
                                    modifier              = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    Text(
                                        count.toString(),
                                        style      = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color      = color,
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(7.dp)
                                        .background(Surface2, MaterialTheme.shapes.extraSmall),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(pct)
                                            .fillMaxHeight()
                                            .background(color, MaterialTheme.shapes.extraSmall),
                                    )
                                }
                            }
                        }

                        // Resumen goles
                        Spacer(Modifier.height(4.dp))
                        HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                        ) {
                            GoalStat("Goles a favor",  ts.golesFavor,  Accent)
                            GoalStat("Goles en contra", ts.golesContra, Error)
                            GoalStat("Diferencia", ts.golesFavor - ts.golesContra,
                                if (ts.golesFavor >= ts.golesContra) Success else Error)
                        }
                    }
                }
            }
        }

        // ── Alertas: jugadores sin evaluación ─────────────────
        item {
            Surface(
                color    = Surface,
                shape    = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint     = Warning,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text       = "Sin evaluación",
                                style      = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color      = TextPrimary,
                            )
                        }
                        TextButton(onClick = { onNavigate("evaluaciones") }) {
                            Text("Evaluar", color = Accent,
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    if (stats.jugadoresSinEvaluacion.isEmpty()) {
                        Box(
                            modifier         = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "✅ Todos los jugadores evaluados",
                                color = Success,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    } else {
                        Spacer(Modifier.height(8.dp))
                        stats.jugadoresSinEvaluacion.forEach { jugador ->
                            Surface(
                                onClick  = { onNavigate("jugadores") },
                                color    = Surface2,
                                shape    = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp),
                            ) {
                                Row(
                                    modifier              = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment     = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text       = "${jugador.nombres} ${jugador.apellidos}",
                                        style      = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color      = TextPrimary,
                                        modifier   = Modifier.weight(1f),
                                        maxLines   = 1,
                                    )
                                    Surface(
                                        color = Warning.copy(alpha = 0.15f),
                                        shape = MaterialTheme.shapes.extraSmall,
                                    ) {
                                        Text(
                                            text       = jugador.posicion,
                                            color      = Warning,
                                            fontSize   = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier   = Modifier.padding(
                                                horizontal = 8.dp, vertical = 3.dp
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Acciones rápidas ──────────────────────────────────
        item {
            Surface(
                color    = Surface,
                shape    = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text       = "⚡ Acciones rápidas",
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                        modifier   = Modifier.padding(bottom = 12.dp),
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(
                            listOf(
                                Triple("Ver plantel",    Accent,  "jugadores"),
                                Triple("Ver partidos",   Info,    "partidos"),
                                Triple("+ Evaluación",  Success, "evaluaciones/crear"),
                                Triple("Mi perfil",      Warning, "perfil"),
                            )
                        ) { (label, color, route) ->
                            Surface(
                                onClick = { onNavigate(route) },
                                color   = color.copy(alpha = 0.1f),
                                shape   = MaterialTheme.shapes.medium,
                            ) {
                                Text(
                                    text       = label,
                                    color      = color,
                                    fontWeight = FontWeight.Bold,
                                    style      = MaterialTheme.typography.bodySmall,
                                    modifier   = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalStat(label: String, valor: Int, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text       = if (valor > 0) "+$valor" else valor.toString(),
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color      = color,
        )
        Text(label, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = TextSecondary)
    }
}