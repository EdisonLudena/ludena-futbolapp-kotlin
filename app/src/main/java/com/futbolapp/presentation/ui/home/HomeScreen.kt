// presentation/ui/home/HomeScreen.kt
package com.futbolapp.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.futbolapp.domain.model.Partido
import com.futbolapp.domain.model.TemporadaStats
import com.futbolapp.presentation.viewmodel.JugadorViewModel
import com.futbolapp.presentation.viewmodel.PartidoViewModel
import com.futbolapp.theme.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle

@Composable
fun HomeScreen(
    onVerJugadores:    () -> Unit,
    onVerPartidos:     () -> Unit,
    onVerEvaluaciones: () -> Unit,
    onIrAlDashboard:   () -> Unit, // <--- NUEVO
    isCoach:           Boolean,    // <--- NUEVO
    jugadorVm: JugadorViewModel = hiltViewModel(),
    partidoVm: PartidoViewModel = hiltViewModel(),
) {
    val jugadoresState by jugadorVm.state.collectAsState()
    val partidosState  by partidoVm.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            partidoVm.loadStats()
        }
    }

    LazyColumn(
        modifier       = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(bottom = 32.dp),
    ) {
        // ── Hero ──────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Surface2, Background),
                        ),
                    )
                    .padding(horizontal = 24.dp, vertical = 40.dp),
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector        = Icons.Default.SportsSoccer,
                            contentDescription = null,
                            tint               = Accent,
                            modifier           = Modifier.size(32.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text       = "FutbolApp",
                            fontSize   = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Accent,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text       = "Gestiona tu equipo",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                    )
                    Text(
                        text  = "Jugadores, partidos y evaluaciones en un solo lugar.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        }

        // ── [NUEVO] Sección Coach Dashboard ──────────────────
        if (isCoach) {
            item {
                SectionTitle("Acceso de Entrenador")
                Button(
                    onClick = onIrAlDashboard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Icon(Icons.Default.Dashboard, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Ir al Dashboard del Coach")
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        // ── Estadísticas de temporada ─────────────────────────
        partidosState.stats?.let { stats ->
            item {
                SectionTitle("Temporada")
                StatsCard(stats = stats)
                Spacer(Modifier.height(8.dp))
            }
        }

        // ── Resumen rápido ─────────────────────────────────────
        item {
            SectionTitle("Resumen")
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ResumenCard(
                    emoji   = "👥",
                    titulo  = "Jugadores",
                    valor   = "${jugadoresState.jugadores.size}",
                    sub     = "en plantilla",
                    onClick = onVerJugadores,
                    modifier = Modifier.weight(1f),
                )
                ResumenCard(
                    emoji   = "⚽",
                    titulo  = "Partidos",
                    valor   = "${partidosState.partidos.size}",
                    sub     = "registrados",
                    onClick = onVerPartidos,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Próximos partidos ──────────────────────────────────
        val proximos = partidosState.partidos
            .filter { it.resultadoFinal == null && it.isActive }
            .take(3)

        if (proximos.isNotEmpty()) {
            item { SectionTitle("Próximos partidos") }
            items(proximos) { partido ->
                ProximoPartidoCard(
                    partido = partido,
                    onClick = onVerPartidos,
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        // ── Accesos rápidos ───────────────────────────────────
        item {
            SectionTitle("Accesos rápidos")
            Column(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement   = Arrangement.spacedBy(10.dp),
            ) {
                AccesoRapidoItem(
                    emoji   = "📋",
                    titulo  = "Ver plantel completo",
                    sub     = "${jugadoresState.jugadores.count { it.isActive }} activos",
                    onClick = onVerJugadores,
                )
                AccesoRapidoItem(
                    emoji   = "📊",
                    titulo  = "Evaluaciones físicas",
                    sub     = "Métricas y calificaciones",
                    onClick = onVerEvaluaciones,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Composables locales ───────────────────────────────────────

@Composable
private fun SectionTitle(title: String) {
    Text(
        text     = title,
        style    = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color    = TextPrimary,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
    )
}

@Composable
private fun StatsCard(stats: TemporadaStats) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Surface, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        StatItem("PJ",  stats.totalPartidos.toString())
        StatItem("V",   stats.victorias.toString(),  Success)
        StatItem("E",   stats.empates.toString(),    Warning)
        StatItem("D",   stats.derrotas.toString(),   Error)
        StatItem("GF",  stats.golesFavor.toString())
        StatItem("GC",  stats.golesContra.toString())
    }
}

@Composable
private fun StatItem(label: String, valor: String, color: androidx.compose.ui.graphics.Color = TextPrimary) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun ResumenCard(
    emoji:   String,
    titulo:  String,
    valor:   String,
    sub:     String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick        = onClick,
        shape          = MaterialTheme.shapes.large,
        color          = Surface,
        tonalElevation = 0.dp,
        modifier       = modifier,
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(emoji, fontSize = 28.sp)
            Spacer(Modifier.height(4.dp))
            Text(valor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Accent)
            Text(titulo, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(sub, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
private fun ProximoPartidoCard(partido: Partido, onClick: () -> Unit) {
    Surface(
        onClick        = onClick,
        shape          = MaterialTheme.shapes.medium,
        color          = Surface,
        tonalElevation = 0.dp,
        modifier       = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("⚽", fontSize = 24.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "vs ${partido.rival}",
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary,
                )
                Text(
                    text  = partido.lugar,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                Text(
                    text  = partido.fecha.take(10), // YYYY-MM-DD
                    style = MaterialTheme.typography.bodySmall,
                    color = Accent,
                )
            }
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextSecondary)
        }
    }
}

@Composable
private fun AccesoRapidoItem(emoji: String, titulo: String, sub: String, onClick: () -> Unit) {
    Surface(
        onClick        = onClick,
        shape          = MaterialTheme.shapes.medium,
        color          = Surface,
        tonalElevation = 0.dp,
        modifier       = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(sub, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextSecondary)
        }
    }
}