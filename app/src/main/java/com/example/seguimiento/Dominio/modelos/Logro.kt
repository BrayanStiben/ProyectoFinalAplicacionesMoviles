package com.example.seguimiento.Dominio.modelos

import androidx.compose.ui.graphics.Color

data class Logro(
    val id: String,
    val tituloResId: Int,
    val descripcionResId: Int,
    val puntos: Int = 100,
    val categoria: String, // "RESCATE", "COMUNIDAD", "PERFIL", "ADOPCION", "SISTEMA"
    val esNegativo: Boolean = false,
    val iconResId: Int = 0
)

fun getLevelColor(level: Int): Color {
    return when (level) {
        1 -> Color(0xFF9E9E9E) // Gris - Novato
        2 -> Color(0xFF4CAF50) // Verde - Colaborador
        3 -> Color(0xFF2196F3) // Azul - Protector
        4 -> Color(0xFF9C27B0) // Morado - Héroe
        else -> Color(0xFFFFD700) // Dorado - Leyenda
    }
}
