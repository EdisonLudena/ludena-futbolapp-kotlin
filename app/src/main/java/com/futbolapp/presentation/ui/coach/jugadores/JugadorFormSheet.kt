// presentation/ui/coach/jugadores/JugadorFormSheet.kt
package com.futbolapp.presentation.ui.coach.jugadores

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.futbolapp.domain.model.Jugador
import com.futbolapp.domain.model.JugadorPayload
import com.futbolapp.presentation.viewmodel.JugadorFormState
import com.futbolapp.theme.*

private val POSICIONES = listOf(
    "Portero", "Defensa Central", "Lateral Derecho", "Lateral Izquierdo",
    "Centrocampista", "Mediapunta", "Extremo Derecho", "Extremo Izquierdo",
    "Delantero", "Segundo Delantero",
)

private val CATEGORIAS = listOf(
    "Senior", "Juvenil", "Sub-20", "Sub-17", "Sub-15", "Femenino",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JugadorFormSheet(
    initial:   Jugador?,
    formState: JugadorFormState,
    onSave:    (JugadorPayload) -> Unit,
    onDismiss: () -> Unit,
) {
    val isEdit = initial != null

    var nombres   by remember { mutableStateOf(initial?.nombres   ?: "") }
    var apellidos by remember { mutableStateOf(initial?.apellidos ?: "") }
    var posicion  by remember { mutableStateOf(initial?.posicion  ?: POSICIONES.first()) }
    var categoria by remember { mutableStateOf(initial?.categoria ?: CATEGORIAS.first()) }
    var fotoUrl   by remember { mutableStateOf(initial?.fotoUrl   ?: "") }
    var isActive  by remember { mutableStateOf(initial?.isActive  ?: true) }

    var posicionExpanded  by remember { mutableStateOf(false) }
    var categoriaExpanded by remember { mutableStateOf(false) }

    // Cerrar al éxito
    LaunchedEffect(formState) {
        if (formState is JugadorFormState.Success) onDismiss()
    }

    val isSaving    = formState is JugadorFormState.Saving
    val nombresError = nombres.isNotEmpty() && nombres.length < 2
    val apellidosError = apellidos.isNotEmpty() && apellidos.length < 2
    val canSave = nombres.length >= 2 && apellidos.length >= 2 && !isSaving

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
            // Título
            Text(
                text       = if (isEdit) "Editar: ${initial?.nombres} ${initial?.apellidos}"
                else "Nuevo jugador",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
            )

            // Error del formulario
            if (formState is JugadorFormState.Error) {
                Surface(
                    color    = Error.copy(alpha = 0.1f),
                    shape    = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text     = formState.message,
                        color    = Error,
                        style    = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }

            // Nombres
            OutlinedTextField(
                value         = nombres,
                onValueChange = { nombres = it },
                label         = { Text("Nombres *") },
                placeholder   = { Text("ej. Carlos", color = TextFaint) },
                isError       = nombresError,
                supportingText = if (nombresError) {
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

            // Apellidos
            OutlinedTextField(
                value         = apellidos,
                onValueChange = { apellidos = it },
                label         = { Text("Apellidos *") },
                placeholder   = { Text("ej. García", color = TextFaint) },
                isError       = apellidosError,
                supportingText = if (apellidosError) {
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

            // Posición
            Text("Posición *", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            ExposedDropdownMenuBox(
                expanded         = posicionExpanded,
                onExpandedChange = { if (!isSaving) posicionExpanded = it },
            ) {
                OutlinedTextField(
                    value         = posicion,
                    onValueChange = {},
                    readOnly      = true,
                    modifier      = Modifier.menuAnchor().fillMaxWidth(),
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(posicionExpanded) },
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Accent,
                        unfocusedBorderColor = Border,
                    ),
                )
                ExposedDropdownMenu(
                    expanded         = posicionExpanded,
                    onDismissRequest = { posicionExpanded = false },
                ) {
                    POSICIONES.forEach { pos ->
                        DropdownMenuItem(
                            text    = { Text(pos) },
                            onClick = { posicion = pos; posicionExpanded = false },
                        )
                    }
                }
            }

            // Categoría
            Text("Categoría *", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            ExposedDropdownMenuBox(
                expanded         = categoriaExpanded,
                onExpandedChange = { if (!isSaving) categoriaExpanded = it },
            ) {
                OutlinedTextField(
                    value         = categoria,
                    onValueChange = {},
                    readOnly      = true,
                    modifier      = Modifier.menuAnchor().fillMaxWidth(),
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(categoriaExpanded) },
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Accent,
                        unfocusedBorderColor = Border,
                    ),
                )
                ExposedDropdownMenu(
                    expanded         = categoriaExpanded,
                    onDismissRequest = { categoriaExpanded = false },
                ) {
                    CATEGORIAS.forEach { cat ->
                        DropdownMenuItem(
                            text    = { Text(cat) },
                            onClick = { categoria = cat; categoriaExpanded = false },
                        )
                    }
                }
            }

            // URL de foto (opcional)
            OutlinedTextField(
                value         = fotoUrl,
                onValueChange = { fotoUrl = it },
                label         = { Text("URL de foto (opcional)") },
                placeholder   = { Text("https://...", color = TextFaint) },
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
                            "Jugador activo",
                            style      = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextPrimary,
                        )
                        Text(
                            "Aparece en el plantel",
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
                    shape    = MaterialTheme.shapes.medium,
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        onSave(
                            JugadorPayload(
                                nombres   = nombres.trim(),
                                apellidos = apellidos.trim(),
                                posicion  = posicion,
                                categoria = categoria,
                                fotoUrl   = fotoUrl.trim().ifBlank { null },
                                isActive  = isActive,
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
                        text       = if (isSaving) "Guardando..."
                        else if (isEdit) "Guardar cambios"
                        else "Crear jugador",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}