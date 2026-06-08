// presentation/ui/partidos/PartidosScreen.kt
package com.futbolapp.presentation.ui.partidos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.futbolapp.presentation.viewmodel.PartidoViewModel
import com.futbolapp.theme.*

@Composable
fun PartidosScreen(
    onPartidoClick: (Int) -> Unit,
    viewModel: PartidoViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadStats() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // ── Cabecera Premium ──────────────────────────────────
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp // Elevación sutil para separar del resto
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Partidos",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    Text(
                        text = "${state.partidos.size} total",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        // ── Stats Rápidas (Estilo moderno) ──────────────────────
        state.stats?.let { stats ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(16.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    MiniStat("PJ", stats.totalPartidos.toString())
                    MiniStat("V", stats.victorias.toString(), MaterialTheme.colorScheme.primary)
                    MiniStat("E", stats.empates.toString(), MaterialTheme.colorScheme.secondary)
                    MiniStat("D", stats.derrotas.toString(), MaterialTheme.colorScheme.error)
                    MiniStat("GF", stats.golesFavor.toString())
                    MiniStat("GC", stats.golesContra.toString())
                }
            }
        }

        // ── Lista de Partidos ─────────────────────────────────
        when {
            state.isLoading -> LoadingScreen("Cargando...")
            state.error != null -> ErrorScreen(state.error!!, viewModel::loadPartidos)
            state.partidos.isEmpty() -> { /* Tu estado vacío actual */ }
            else -> {
                val proximos = state.partidos.filter { it.resultadoFinal == null && it.isActive }
                val finalizados = state.partidos.filter { it.resultadoFinal != null }

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (proximos.isNotEmpty()) {
                        item { Text("Próximos", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(8.dp)) }
                        items(proximos, key = { it.id }) { PartidoCard(it) { onPartidoClick(it.id) } }
                    }

                    if (finalizados.isNotEmpty()) {
                        item { Text("Finalizados", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(8.dp)) }
                        items(finalizados, key = { it.id }) { PartidoCard(it) { onPartidoClick(it.id) } }
                    }
                }
            }
        }
    }
}

@Composable
fun PartidoCard(partido: Partido, onClick: () -> Unit) {
    val esFinalizado = partido.resultadoFinal != null

    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp, // Elevación M3 para profundidad
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icono con fondo estilo M3
            Surface(
                color = if (esFinalizado) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(if (esFinalizado) "✓" else "⚽", fontSize = 20.sp)
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("vs ${partido.rival}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(partido.lugar, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(partido.fecha.take(10), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }

            // Badge de resultado
            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (esFinalizado) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Text(
                    text = partido.resultadoFinal ?: "Pendiente",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun MiniStat(
    label: String,
    valor: String,
    color: androidx.compose.ui.graphics.Color = TextPrimary,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}