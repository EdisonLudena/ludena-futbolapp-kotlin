// presentation/ui/jugadores/JugadoresScreen.kt
package com.futbolapp.presentation.ui.jugadores

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.futbolapp.domain.model.Jugador
import com.futbolapp.presentation.components.ErrorScreen
import com.futbolapp.presentation.components.LoadingScreen
import com.futbolapp.presentation.viewmodel.JugadorViewModel
import com.futbolapp.theme.*

private val POSICIONES  = listOf("Portero", "Defensa", "Centrocampista", "Delantero")
private val CATEGORIAS  = listOf("Senior", "Juvenil", "Sub-20", "Sub-17")

@Composable
fun JugadoresScreen(
    onJugadorClick: (Int) -> Unit,
    viewModel: JugadorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // ── Cabecera con búsqueda ──────────────────────────────
        Surface(color = Surface, tonalElevation = 0.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text(
                        text       = "Jugadores",
                        style      = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                    )
                    Text(
                        text  = "${state.jugadoresFiltrados.size} jugadores",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value         = state.search,
                    onValueChange = viewModel::setSearch,
                    placeholder   = { Text("Buscar por nombre o posición...", color = TextFaint) },
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

                // Filtro por posición
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = state.filtroPosicion == null,
                            onClick  = { viewModel.setFiltroPosicion(null) },
                            label    = { Text("Todas", style = MaterialTheme.typography.labelSmall) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Accent,
                                selectedLabelColor     = AccentOnDark,
                                containerColor         = Surface2,
                                labelColor             = TextSecondary,
                            ),
                        )
                    }
                    items(POSICIONES) { pos ->
                        FilterChip(
                            selected = state.filtroPosicion == pos,
                            onClick  = { viewModel.setFiltroPosicion(pos) },
                            label    = { Text(pos, style = MaterialTheme.typography.labelSmall) },
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
            state.isLoading -> LoadingScreen("Cargando jugadores...")
            state.error != null -> ErrorScreen(
                message = state.error!!,
                onRetry = viewModel::loadJugadores,
            )
            state.jugadoresFiltrados.isEmpty() -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", style = MaterialTheme.typography.displayMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("Sin resultados", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("Prueba con otro nombre o posición", color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding        = PaddingValues(16.dp),
                    verticalArrangement   = Arrangement.spacedBy(10.dp),
                    modifier              = Modifier.fillMaxSize(),
                ) {
                    items(state.jugadoresFiltrados, key = { it.id }) { jugador ->
                        JugadorCard(
                            jugador = jugador,
                            onClick = { onJugadorClick(jugador.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JugadorCard(jugador: Jugador, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp), // Padding fuera
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                if (jugador.fotoUrl != null) {
                    AsyncImage(
                        model = jugador.fotoUrl,
                        contentDescription = jugador.nombres,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Text(
                        text = jugador.nombres.take(1).uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Accent,
                    )
                }
            }

            // Info del jugador
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center // Centra verticalmente el contenido dentro de la columna
            ) {
                Text(
                    text = "${jugador.nombres} ${jugador.apellidos}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1, // Evita que nombres muy largos rompan el diseño
                    overflow = TextOverflow.Ellipsis // Agrega "..." si el nombre es demasiado largo
                )

                Spacer(Modifier.height(6.dp)) // Un poco más de aire entre texto y chips

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically // Asegura que los chips estén alineados
                ) {
                    PosicionChip(jugador.posicion)
                    CategoriaChip(jugador.categoria)
                }
            }
            StatusChip(isActive = jugador.isActive)
        }
    }
}

@Composable
private fun PosicionChip(posicion: String) {
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = Accent.copy(alpha = 0.15f),
    ) {
        Text(
            text     = posicion,
            style    = MaterialTheme.typography.labelSmall,
            color    = AccentLight,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun CategoriaChip(categoria: String) {
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = Surface2,
    ) {
        Text(
            text     = categoria,
            style    = MaterialTheme.typography.labelSmall,
            color    = TextSecondary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun StatusChip(isActive: Boolean) {
    val color = if (isActive) Success else TextFaint
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(99.dp)
    ) {
        Text(
            text = if (isActive) "● Activo" else "● Inactivo",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}