// presentation/ui/coach/partidos/ResultadoDialog.kt
package com.futbolapp.presentation.ui.coach.partidos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.futbolapp.domain.model.Partido
import com.futbolapp.theme.*

@Composable
fun ResultadoDialog(
    partido:           Partido,
    onActualizar:      (String) -> Unit,
    onDismiss:         () -> Unit,
) {
    var resultado  by remember { mutableStateOf(partido.resultadoFinal ?: "") }
    var isLoading  by remember { mutableStateOf(false) }
    var feedback   by remember { mutableStateOf<String?>(null) }

    val resultadoValido = resultado.matches(Regex("""\d+-\d+"""))
    val canGuardar      = resultadoValido && !isLoading

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        containerColor   = Surface,
        shape            = MaterialTheme.shapes.large,
        title = {
            Column {
                Text(
                    "Resultado: vs ${partido.rival}",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    partido.fecha.take(10),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value         = resultado,
                    onValueChange = { resultado = it; feedback = null },
                    label         = { Text("Marcador *") },
                    placeholder   = { Text("ej. 2-1", color = TextFaint) },
                    singleLine    = true,
                    enabled       = !isLoading,
                    isError       = resultado.isNotEmpty() && !resultadoValido,
                    supportingText = if (resultado.isNotEmpty() && !resultadoValido) {
                        { Text("Formato: goles-goles (ej. 2-1)", color = Error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    colors   = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Accent,
                        unfocusedBorderColor = Border,
                    ),
                )
                feedback?.let {
                    Text(
                        it,
                        style      = MaterialTheme.typography.bodySmall,
                        color      = if (it.startsWith("Error")) Error else Success,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { isLoading = true; onActualizar(resultado) },
                enabled = canGuardar,
                colors  = ButtonDefaults.buttonColors(
                    containerColor         = Accent,
                    contentColor           = AccentOnDark,
                    disabledContainerColor = Accent.copy(alpha = 0.4f),
                ),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color       = AccentOnDark,
                        modifier    = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Text(if (isLoading) "Guardando..." else "Guardar resultado",
                    fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!isLoading) onDismiss() }) {
                Text("Cancelar", color = TextSecondary)
            }
        },
    )
}