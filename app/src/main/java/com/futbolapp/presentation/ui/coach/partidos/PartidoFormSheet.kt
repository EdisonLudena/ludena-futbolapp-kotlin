// presentation/ui/coach/partidos/PartidoFormSheet.kt
package com.futbolapp.presentation.ui.coach.partidos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.futbolapp.domain.model.Partido
import com.futbolapp.domain.model.PartidoPayload
import com.futbolapp.presentation.viewmodel.PartidoFormState
import com.futbolapp.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartidoFormSheet(
    initial:   Partido?,
    formState: PartidoFormState,
    onSave:    (PartidoPayload) -> Unit,
    onDismiss: () -> Unit,
) {
    val isEdit = initial != null

    // Extraer solo la fecha ISO sin la parte de la hora para edición
    var rival    by remember { mutableStateOf(initial?.rival ?: "") }
    var fecha    by remember { mutableStateOf(initial?.fecha?.take(10) ?: "") }
    var hora     by remember { mutableStateOf(
        if (initial?.fecha != null && initial.fecha.length >= 16)
            initial.fecha.substring(11, 16)
        else "15:00"
    )}
    var lugar    by remember { mutableStateOf(initial?.lugar ?: "") }
    var isActive by remember { mutableStateOf(initial?.isActive ?: true) }

    val isSaving    = formState is PartidoFormState.Saving
    val rivalError  = rival.isNotEmpty() && rival.length < 2
    val fechaError  = fecha.isNotEmpty() && !fecha.matches(Regex("""\d{4}-\d{2}-\d{2}"""))
    val horaError   = hora.isNotEmpty() && !hora.matches(Regex("""\d{2}:\d{2}"""))
    val lugarError  = lugar.isNotEmpty() && lugar.length < 2
    val canSave     = rival.length >= 2 && !fechaError && fecha.isNotEmpty() &&
            !horaError && hora.isNotEmpty() &&
            lugar.length >= 2 && !isSaving

    // Construir ISO 8601 combinando fecha y hora
    val fechaIso = if (fecha.isNotBlank() && hora.isNotBlank()) "${fecha}T${hora}:00Z" else ""

    LaunchedEffect(formState) {
        if (formState is PartidoFormState.Success) onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = { if (!isSaving) onDismiss() },
        containerColor   = Surface,
        dragHandle = {
            Box(
                modifier         = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    modifier = Modifier.size(40.dp, 4.dp),
                    color    = Border,
                    shape    = MaterialTheme.shapes.extraSmall,
                ) {}
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text       = if (isEdit) "Editar: vs ${initial?.rival}" else "Nuevo partido",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
            )

            // Error global
            if (formState is PartidoFormState.Error) {
                Surface(
                    color    = Error.copy(alpha = 0.1f),
                    shape    = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        formState.message, color = Error,
                        style    = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }

            // Rival
            OutlinedTextField(
                value         = rival,
                onValueChange = { rival = it },
                label         = { Text("Rival *") },
                placeholder   = { Text("ej. Club Deportivo Norte", color = TextFaint) },
                isError       = rivalError,
                supportingText = if (rivalError) {
                    { Text("Mínimo 2 caracteres", color = Error) }
                } else null,
                singleLine    = true,
                enabled       = !isSaving,
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Accent,
                    unfocusedBorderColor = Border,
                ),
            )

            // Fecha y Hora en fila
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value         = fecha,
                    onValueChange = { fecha = it },
                    label         = { Text("Fecha *") },
                    placeholder   = { Text("AAAA-MM-DD", color = TextFaint) },
                    isError       = fechaError,
                    supportingText = if (fechaError) {
                        { Text("Formato: 2026-06-15", color = Error) }
                    } else null,
                    singleLine    = true,
                    enabled       = !isSaving,
                    modifier      = Modifier.weight(1.5f),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Accent,
                        unfocusedBorderColor = Border,
                    ),
                )
                OutlinedTextField(
                    value         = hora,
                    onValueChange = { hora = it },
                    label         = { Text("Hora *") },
                    placeholder   = { Text("HH:MM", color = TextFaint) },
                    isError       = horaError,
                    singleLine    = true,
                    enabled       = !isSaving,
                    modifier      = Modifier.weight(1f),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Accent,
                        unfocusedBorderColor = Border,
                    ),
                )
            }
            if (fechaIso.isNotBlank() && !fechaError && !horaError) {
                Text(
                    "Guardará como: $fechaIso",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextFaint,
                )
            }

            // Lugar
            OutlinedTextField(
                value         = lugar,
                onValueChange = { lugar = it },
                label         = { Text("Lugar *") },
                placeholder   = { Text("ej. Estadio Municipal", color = TextFaint) },
                isError       = lugarError,
                supportingText = if (lugarError) {
                    { Text("Mínimo 2 caracteres", color = Error) }
                } else null,
                singleLine    = true,
                enabled       = !isSaving,
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Accent,
                    unfocusedBorderColor = Border,
                ),
            )

            // Toggle activo
            Surface(
                color    = Surface2,
                shape    = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            "Partido activo",
                            style      = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextPrimary,
                        )
                        Text(
                            "Visible en el calendario",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                    Switch(
                        checked         = isActive,
                        onCheckedChange = { isActive = it },
                        enabled         = !isSaving,
                        colors          = SwitchDefaults.colors(
                            checkedThumbColor    = AccentOnDark,
                            checkedTrackColor    = Accent,
                            uncheckedTrackColor  = Surface2,
                            uncheckedBorderColor = Border,
                        ),
                    )
                }
            }

            // Botones
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick  = { if (!isSaving) onDismiss() },
                    enabled  = !isSaving,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    border   = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Border),
                    ),
                    shape = MaterialTheme.shapes.medium,
                ) { Text("Cancelar") }

                Button(
                    onClick = {
                        onSave(
                            PartidoPayload(
                                rival    = rival.trim(),
                                fecha    = fechaIso,
                                lugar    = lugar.trim(),
                                isActive = isActive,
                            )
                        )
                    },
                    enabled  = canSave,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = Accent,
                        contentColor           = AccentOnDark,
                        disabledContainerColor = Accent.copy(alpha = 0.4f),
                    ),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color       = AccentOnDark,
                            modifier    = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        if (isSaving) "Guardando..."
                        else if (isEdit) "Guardar cambios"
                        else "Crear partido",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}