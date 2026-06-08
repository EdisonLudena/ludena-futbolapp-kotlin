// presentation/ui/evaluaciones/CrearEvaluacionScreen.kt
package com.futbolapp.presentation.ui.evaluaciones

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
import com.futbolapp.domain.model.EvaluacionPayload
import com.futbolapp.domain.model.Jugador
import com.futbolapp.presentation.components.FutbolButton
import com.futbolapp.presentation.components.calificacionColor
import com.futbolapp.presentation.viewmodel.CrearEvaluacionState
import com.futbolapp.presentation.viewmodel.EvaluacionViewModel
import com.futbolapp.presentation.viewmodel.JugadorViewModel
import com.futbolapp.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearEvaluacionScreen(
    onBack:        () -> Unit,
    onCreado:      () -> Unit,
    evaluacionVm:  EvaluacionViewModel = hiltViewModel(),
    jugadorVm:     JugadorViewModel    = hiltViewModel(),
) {
    val crearState by evaluacionVm.crearState.collectAsState()
    val jugadores  by jugadorVm.state.collectAsState()

    // Campos del formulario
    var jugadorSeleccionado by remember { mutableStateOf<Jugador?>(null) }
    var jugadorExpanded     by remember { mutableStateOf(false) }
    var pesoKg              by remember { mutableStateOf("") }
    var alturaCm            by remember { mutableStateOf("") }
    var velocidadSeg        by remember { mutableStateOf("") }
    var calificacion        by remember { mutableStateOf(50f) }
    var notas               by remember { mutableStateOf("") }

    // Navegar al éxito
    LaunchedEffect(crearState) {
        if (crearState is CrearEvaluacionState.Success) {
            evaluacionVm.clearCrearState()
            onCreado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // ── Header ────────────────────────────────────────────
        Surface(color = Surface, tonalElevation = 0.dp) {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                }
                Text(
                    text       = "Nueva evaluación",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            // ── Selector de jugador ───────────────────────────
            FormSection(title = "Jugador") {
                ExposedDropdownMenuBox(
                    expanded         = jugadorExpanded,
                    onExpandedChange = { jugadorExpanded = it },
                ) {
                    OutlinedTextField(
                        value         = jugadorSeleccionado
                            ?.let { "${it.nombres} ${it.apellidos}" } ?: "",
                        onValueChange = {},
                        readOnly      = true,
                        placeholder   = { Text("Seleccionar jugador", color = TextFaint) },
                        modifier      = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(jugadorExpanded) },
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Accent,
                            unfocusedBorderColor = Border,
                        ),
                    )
                    ExposedDropdownMenu(
                        expanded         = jugadorExpanded,
                        onDismissRequest = { jugadorExpanded = false },
                    ) {
                        jugadores.jugadores.filter { it.isActive }.forEach { j ->
                            DropdownMenuItem(
                                text    = { Text("${j.nombres} ${j.apellidos} — ${j.posicion}") },
                                onClick = { jugadorSeleccionado = j; jugadorExpanded = false },
                            )
                        }
                    }
                }
            }

            // ── Métricas físicas ──────────────────────────────
            FormSection(title = "Métricas físicas") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DecimalField(
                        label    = "Peso (kg)",
                        value    = pesoKg,
                        onChange = { pesoKg = it },
                        modifier = Modifier.weight(1f),
                    )
                    IntField(
                        label    = "Altura (cm)",
                        value    = alturaCm,
                        onChange = { alturaCm = it },
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(4.dp))
                DecimalField(
                    label    = "Velocidad 40m (seg)",
                    value    = velocidadSeg,
                    onChange = { velocidadSeg = it },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // ── Calificación técnica con Slider ───────────────
            FormSection(title = "Calificación técnica") {
                Column {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically,
                    ) {
                        Text(
                            text  = "Calificación",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )
                        Text(
                            text       = "${calificacion.toInt()}/100",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = calificacionColor(calificacion.toInt()),
                        )
                    }
                    Slider(
                        value         = calificacion,
                        onValueChange = { calificacion = it },
                        valueRange    = 0f..100f,
                        steps         = 99,
                        colors        = SliderDefaults.colors(
                            thumbColor       = calificacionColor(calificacion.toInt()),
                            activeTrackColor = calificacionColor(calificacion.toInt()),
                            inactiveTrackColor = Surface2,
                        ),
                    )
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("0", style = MaterialTheme.typography.labelSmall, color = TextFaint)
                        Text("25", style = MaterialTheme.typography.labelSmall, color = TextFaint)
                        Text("50", style = MaterialTheme.typography.labelSmall, color = TextFaint)
                        Text("75", style = MaterialTheme.typography.labelSmall, color = TextFaint)
                        Text("100", style = MaterialTheme.typography.labelSmall, color = TextFaint)
                    }
                }
            }

            // ── Notas del entrenador ──────────────────────────
            FormSection(title = "Notas (opcional)") {
                OutlinedTextField(
                    value         = notas,
                    onValueChange = { notas = it },
                    placeholder   = { Text("Observaciones, comentarios...", color = TextFaint) },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    maxLines      = 5,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Accent,
                        unfocusedBorderColor = Border,
                    ),
                )
            }

            // ── Error ─────────────────────────────────────────
            if (crearState is CrearEvaluacionState.Error) {
                Surface(
                    color    = Error.copy(alpha = 0.1f),
                    shape    = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text     = (crearState as CrearEvaluacionState.Error).message,
                        color    = Error,
                        style    = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }

            // ── Botón ─────────────────────────────────────────
            val canSubmit = jugadorSeleccionado != null &&
                    pesoKg.toDoubleOrNull() != null &&
                    alturaCm.toIntOrNull() != null &&
                    velocidadSeg.toDoubleOrNull() != null

            FutbolButton(
                text      = "Guardar evaluación",
                onClick   = {
                    if (canSubmit) {
                        evaluacionVm.crearEvaluacion(
                            EvaluacionPayload(
                                jugador             = jugadorSeleccionado!!.id,
                                pesoKg              = pesoKg.toDouble(),
                                alturaCm            = alturaCm.toInt(),
                                velocidadSeg        = velocidadSeg.toDouble(),
                                calificacionTecnica = calificacion.toInt(),
                                notasComentario     = notas,
                            )
                        )
                    }
                },
                isLoading = crearState is CrearEvaluacionState.Loading,
                enabled   = canSubmit,
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(shape = MaterialTheme.shapes.large, color = Surface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text          = title,
                style         = MaterialTheme.typography.labelSmall,
                color         = TextSecondary,
                letterSpacing = 0.8.sp,
                modifier      = Modifier.padding(bottom = 12.dp),
            )
            content()
        }
    }
}

@Composable
private fun DecimalField(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value           = value,
        onValueChange   = { if (it.matches(Regex("""^\d*\.?\d*$"""))) onChange(it) },
        label           = { Text(label) },
        placeholder     = { Text("0.0", color = TextFaint) },
        singleLine      = true,
        modifier        = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        colors          = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = Accent,
            unfocusedBorderColor = Border,
        ),
    )
}

@Composable
private fun IntField(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
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