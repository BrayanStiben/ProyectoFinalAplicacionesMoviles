package com.example.seguimiento.Dominio.modelos

import androidx.compose.ui.graphics.Color

enum class UserRole {
    USER, MODERATOR, ADMIN
}

data class User(
    val id: String,
    val name: String,
    val city: String,
    val departamento: String,
    val address: String,
    val email: String,
    val password: String,
    val profilePictureUrl: String,
    val role: UserRole = UserRole.USER,
    val isBanned: Boolean = false,
    val banReason: String = "",
    val points: Int = 0,
    val badges: List<String> = emptyList(),
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val rejectionCount: Int = 0,
    val penaltyEndTime: Long = 0
) {
    // Rangos ajustados para ser alcanzables con los logros actuales (100 pts c/u)
    val level: Int get() = when {
        points >= 1500 -> 5 // Leyenda
        points >= 1000 -> 4 // Héroe
        points >= 700 -> 3  // Protector
        points >= 400 -> 2  // Colaborador
        else -> 1           // Novato
    }

    fun getLevelName(): String = when (level) {
        1 -> "Novato"
        2 -> "Colaborador"
        3 -> "Protector"
        4 -> "Héroe"
        else -> "Leyenda"
    }

    fun getLevelColor(): Color = when (level) {
        1 -> Color(0xFF9E9E9E) // Gris
        2 -> Color(0xFF4CAF50) // Verde
        3 -> Color(0xFF2196F3) // Azul
        4 -> Color(0xFF9C27B0) // Morado
        else -> Color(0xFFFFD700) // Dorado
    }
    
    val isPenalized: Boolean 
        get() = System.currentTimeMillis() < penaltyEndTime
}
