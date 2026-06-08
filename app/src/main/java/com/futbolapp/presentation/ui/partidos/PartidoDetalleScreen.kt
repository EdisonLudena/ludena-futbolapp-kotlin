// presentation/ui/partidos/PartidoDetalleScreen.kt
package com.futbolapp.presentation.ui.partidos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.futbolapp.domain.model.Jugador
import com.futbolapp.domain.model.Partido
import com.futbolapp.domain.model.RendimientoPayload
import com.futbolapp.presentation.components.ErrorScreen
import com.futbolapp.presentation.components.FutbolButton
import com.futbolapp.presentation.components.LoadingScreen
import com.futbolapp.presentation.viewmodel.FormState
import com.futbolapp.presentation.viewmodel.PartidoDetalleUiState
import com.futbolapp.presentation.viewmodel.PartidoDetalleViewModel
import com.futbolapp.theme.*

@Composable
fun PartidoDetalleScreen(
    partidoId: Int,
    onBack:    () -> Unit,
    isCoach:   Boolean = false,
    viewModel: PartidoDetalleViewModel = hiltViewModel(),
) {
    val state           by viewModel.state.collectAsState()
    val jugadores       by viewModel.jugadores.collectAsState()
    val rendimientoForm by viewModel.rendimientoForm.collectAsState()
    val resultadoForm   by viewModel.resultadoForm.collectAsState()

    LaunchedEffect(partidoId) { viewModel.load(partidoId) }

    when (val s = state) {
        is PartidoDetalleUiState.Loading -> LoadingScreen("Cargando partido...")
        is PartidoDetalleUiState.Error   -> ErrorScreen(s.message) { viewModel.load(partidoId) }
        is PartidoDetalleUiState.Success -> PartidoDetalleContent(
            partido                = s.partido,
            jugadores              = jugadores,
            isCoach                = isCoach,
            rendimientoForm        = rendimientoForm,
            resultadoForm          = resultadoForm,
            onBack                 = onBack,
            onRegistrarRendimiento = { payload -> viewModel.registrarRendimiento(s.partido.id, payload) },
            onActualizarResultado  = { resultado -> viewModel.actualizarResultado(s.partido, resultado) },
            onClearRendimiento     = viewModel::clearRendimientoForm,
            onClearResultado       = viewModel::clearResultadoForm,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PartidoDetalleContent(
    partido: Partido,
    jugadores: List<Jugador>,
    isCoach: Boolean,
    rendimientoForm: FormState,
    resultadoForm: FormState,
    onBack: () -> Unit,
    onRegistrarRendimiento: (RendimientoPayload) -> Unit,
    onActualizarResultado: (String) -> Unit,
    onClearRendimiento: () -> Unit,
    onClearResultado: () -> Unit,
) {
    var jugadorSeleccionado by remember { mutableStateOf<Jugador?>(null) }
    var jugadorExpanded     by remember { mutableStateOf(false) }
    var minutos             by remember { mutableStateOf("") }
    var goles               by remember { mutableStateOf("") }
    var asistencias         by remember { mutableStateOf("") }
    var amarillas           by remember { mutableStateOf("0") }
    var tarjetaRoja         by remember { mutableStateOf(false) }
    var resultado           by remember { mutableStateOf(partido.resultadoFinal ?: "") }

    LaunchedEffect(rendimientoForm) {
        if (rendimientoForm is FormState.Success) {
            jugadorSeleccionado = null
            minutos = ""; goles = ""; asistencias = ""; amarillas = "0"; tarjetaRoja = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        // ── Header Premium ────────────────────────────────────
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp // Elevación para separación clara
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                Column(modifier = Modifier.weight(1f).padding(end = 48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("vs ${partido.rival}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(partido.fecha.take(10), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            // ── Resultado ─────────────────────────────────────
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("RESULTADO FINAL", style = MaterialTheme.typography.labelSmall, letterSpacing = 1.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = partido.resultadoFinal ?: "Pendiente",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (partido.resultadoFinal != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isCoach) {
                // ── Formulario de Resultado ────────────────────
                Spacer(Modifier.height(16.dp))
                FormCard("Actualizar marcador") {
                    OutlinedTextField(
                        value = resultado,
                        onValueChange = { resultado = it; onClearResultado() },
                        label = { Text("Marcador final (ej: 2-1)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { onActualizarResultado(resultado) },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        enabled = resultado.isNotBlank(),
                        shape = MaterialTheme.shapes.medium
                    ) { Text("Guardar resultado") }
                    FormFeedback(resultadoForm)
                }

                // ── Formulario de Rendimiento ──────────────────

            }
            }
        }
    }



@Composable
private fun FormCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun FormFeedback(formState: FormState) {
    when (formState) {
        is FormState.Success -> Surface(
            color    = Success.copy(alpha = 0.1f),
            shape    = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(formState.msg, color = Success,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(12.dp))
        }
        is FormState.Error -> Surface(
            color    = Error.copy(alpha = 0.1f),
            shape    = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(formState.message, color = Error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(12.dp))
        }
        else -> {}
    }
}

@Composable
private fun NumericField(
    label:    String,
    value:    String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value           = value,
        onValueChange   = { if (it.all { c -> c.isDigit() }) onChange(it) },
        label           = { Text(label) },
        placeholder     = { Text("0", color = TextFaint) },
        singleLine      = true,
        modifier        = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors          = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = Accent,
            unfocusedBorderColor = Border,
        ),
    )
}