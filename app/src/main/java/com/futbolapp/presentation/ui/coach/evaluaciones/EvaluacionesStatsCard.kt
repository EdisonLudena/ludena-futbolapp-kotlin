// presentation/ui/coach/evaluaciones/EvaluacionesStatsCard.kt
package com.futbolapp.presentation.ui.coach.evaluaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.futbolapp.domain.model.Evaluacion
import com.futbolapp.presentation.components.calificacionColor
import com.futbolapp.theme.*

@Composable
fun EvaluacionesStatsCard(evaluaciones: List<Evaluacion>) {
    if (evaluaciones.isEmpty()) return

    val promedio     = evaluaciones.map { it.calificacionTecnica }.average()
    val excelentes   = evaluaciones.count { it.calificacionTecnica >= 80 }
    val buenos       = evaluaciones.count { it.calificacionTecnica in 60..79 }
    val regulares    = evaluaciones.count { it.calificacionTecnica in 40..59 }
    val bajos        = evaluaciones.count { it.calificacionTecnica < 40 }
    val total        = evaluaciones.size.toFloat().coerceAtLeast(1f)

    Surface(
        color    = Surface,
        shape    = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text(
                    "Resumen de calificaciones",
                    style      = MaterialTheme.typography.labelSmall,
                    color      = TextSecondary,
                    letterSpacing = 0.8.sp,
                )
                Text(
                    "${"%.0f".format(promedio)}/100",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color      = calificacionColor(promedio.toInt()),
                )
            }

            Spacer(Modifier.height(14.dp))

            // Barra de distribución por nivel
            listOf(
                Triple("Excelente", excelentes, Success),
                Triple("Bueno",     buenos,     Accent),
                Triple("Regular",   regulares,  Warning),
                Triple("Bajo",      bajos,      Error),
            ).forEach { (label, count, color) ->
                if (count == 0) return@forEach
                val pct = (count / total).coerceIn(0.02f, 1f)
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(
                            count.toString(),
                            style      = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color      = color,
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(Surface2, MaterialTheme.shapes.extraSmall),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(pct)
                                .fillMaxHeight()
                                .background(color, MaterialTheme.shapes.extraSmall),
                        )
                    }
                }
            }
        }
    }
}