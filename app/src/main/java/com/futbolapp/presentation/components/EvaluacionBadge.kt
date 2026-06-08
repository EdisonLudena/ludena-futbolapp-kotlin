// presentation/components/EvaluacionBadge.kt
package com.futbolapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.futbolapp.theme.*

fun calificacionColor(nota: Int): Color = when {
    nota >= 80 -> Success
    nota >= 60 -> Accent
    nota >= 40 -> Warning
    else       -> Error
}

fun calificacionLabel(nota: Int): String = when {
    nota >= 80 -> "Excelente"
    nota >= 60 -> "Bueno"
    nota >= 40 -> "Regular"
    else       -> "Bajo"
}

@Composable
fun CalificacionBadge(nota: Int, modifier: Modifier = Modifier) {
    val color = calificacionColor(nota)
    Row(
        modifier              = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, RoundedCornerShape(50)),
        )
        Text(
            text          = "${nota}/100 · ${calificacionLabel(nota)}",
            color         = color,
            fontSize      = 11.sp,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 0.3.sp,
        )
    }
}