package com.futbolapp.presentation.ui.coach.evaluaciones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.futbolapp.domain.model.EvaluacionPayload
import com.futbolapp.presentation.components.ErrorScreen
import com.futbolapp.presentation.components.LoadingScreen
import com.futbolapp.presentation.viewmodel.EvaluacionDetalleUiState
import com.futbolapp.presentation.viewmodel.EvaluacionViewModel

@Composable
fun EditarEvaluacionScreen(
    evaluacionId: Int,
    onBack: () -> Unit,
    onEditado: () -> Unit,
    viewModel: EvaluacionViewModel = hiltViewModel()
) {
    val detalleState by viewModel.detalleState.collectAsState()

    LaunchedEffect(evaluacionId) {
        viewModel.loadDetalle(evaluacionId)
    }

    // Estados locales para todos los campos del formulario
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var velocidad by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    LaunchedEffect(detalleState) {
        if (detalleState is EvaluacionDetalleUiState.Success) {
            val ev = (detalleState as EvaluacionDetalleUiState.Success).ev
            peso = ev.pesoKg.toString()
            altura = ev.alturaCm.toString()
            velocidad = ev.velocidadSeg.toString()
            calificacion = ev.calificacionTecnica.toString()
            notas = ev.notasComentario
        }
    }

    Scaffold { padding ->
        when (val state = detalleState) {
            is EvaluacionDetalleUiState.Loading -> LoadingScreen("Cargando...")
            is EvaluacionDetalleUiState.Error -> ErrorScreen(state.message) { viewModel.loadDetalle(evaluacionId) }
            is EvaluacionDetalleUiState.Success -> {
                val ev = state.ev
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()) // Scroll para que no se corte en pantallas pequeñas
                ) {
                    Text("Editar Evaluación", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(value = peso, onValueChange = { peso = it }, label = { Text("Peso (kg)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = altura, onValueChange = { altura = it }, label = { Text("Altura (cm)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = velocidad, onValueChange = { velocidad = it }, label = { Text("Velocidad (seg)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = calificacion, onValueChange = { calificacion = it }, label = { Text("Calificación (0-100)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = notas, onValueChange = { notas = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

                    Spacer(Modifier.height(24.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val payload = EvaluacionPayload(
                                jugador = ev.jugadorId,
                                pesoKg = peso.toDoubleOrNull() ?: 0.0,
                                alturaCm = altura.toIntOrNull() ?: 0,
                                velocidadSeg = velocidad.toDoubleOrNull() ?: 0.0,
                                calificacionTecnica = calificacion.toIntOrNull() ?: 0,
                                notasComentario = notas
                            )
                            viewModel.updateEvaluacion(evaluacionId, payload)
                            onEditado()
                        }
                    ) {
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }
}