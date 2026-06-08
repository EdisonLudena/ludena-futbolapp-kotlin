package com.futbolapp.presentation.ui.evaluaciones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.futbolapp.domain.model.Evaluacion
import com.futbolapp.domain.model.EvaluacionPayload
import com.futbolapp.presentation.viewmodel.EvaluacionCoachFormState
import com.futbolapp.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluacionFormSheet(
    initial:   Evaluacion?,
    formState: EvaluacionCoachFormState,
    onSave:    (EvaluacionPayload) -> Unit,
    onDismiss: () -> Unit,
) {
    val isEdit = initial != null
    // Usamos toString() para manejar la conversión a String del TextField
    var jugadorId by remember { mutableStateOf(initial?.jugadorId?.toString() ?: "") }
    var peso      by remember { mutableStateOf(initial?.pesoKg?.toString() ?: "") }
    var altura    by remember { mutableStateOf(initial?.alturaCm?.toString() ?: "") }
    var velocidad by remember { mutableStateOf(initial?.velocidadSeg?.toString() ?: "") }
    var tecnica   by remember { mutableStateOf(initial?.calificacionTecnica?.toString() ?: "") }
    var notas     by remember { mutableStateOf(initial?.notasComentario ?: "") }

    val isSaving = formState is EvaluacionCoachFormState.Saving
    val canSave  = jugadorId.isNotBlank() && !isSaving

    LaunchedEffect(formState) { if (formState is EvaluacionCoachFormState.Success) onDismiss() }

    ModalBottomSheet(
        onDismissRequest = { if (!isSaving) onDismiss() },
        containerColor = Surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(if (isEdit) "Editar Evaluación" else "Nueva Evaluación", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            if (formState is EvaluacionCoachFormState.Error) Text(formState.message, color = Error)

            OutlinedTextField(value = jugadorId, onValueChange = { jugadorId = it }, label = { Text("ID Jugador *") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = peso, onValueChange = { peso = it }, label = { Text("Peso (kg)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(value = altura, onValueChange = { altura = it }, label = { Text("Altura (cm)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = velocidad, onValueChange = { velocidad = it }, label = { Text("Velocidad (seg)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(value = tecnica, onValueChange = { tecnica = it }, label = { Text("Calif. Técnica") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = notas, onValueChange = { notas = it }, label = { Text("Comentarios") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                Button(
                    onClick = {
                        // AQUÍ ESTABA EL ERROR: Cambiado 'jugadorId =' por 'jugador ='
                        onSave(EvaluacionPayload(
                            jugador = jugadorId.toIntOrNull() ?: 0,
                            pesoKg = peso.toDoubleOrNull() ?: 0.0,
                            alturaCm = altura.toIntOrNull() ?: 0,
                            velocidadSeg = velocidad.toDoubleOrNull() ?: 0.0,
                            calificacionTecnica = tecnica.toIntOrNull() ?: 0,
                            notasComentario = notas
                        ))
                    },
                    enabled = canSave,
                    modifier = Modifier.weight(1f)
                ) { Text(if (isSaving) "Guardando..." else "Guardar") }
            }
        }
    }
}