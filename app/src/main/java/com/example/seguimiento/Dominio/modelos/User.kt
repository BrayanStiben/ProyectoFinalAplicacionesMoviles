package com.example.seguimiento.Dominio.modelos

import androidx.compose.ui.graphics.Color
import com.example.seguimiento.R

enum class UserRole {
    USER, MODERATOR, ADMIN
}

data class User(
    val id: String = "",
    val name: String = "",
    val city: String = "",
    val departamento: String = "",
    val address: String = "",
    val email: String = "",
    val password: String = "",
    val profilePictureUrl: String = "",
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
    val level: Int get() = when {
        points >= 1500 -> 5
        points >= 1000 -> 4
        points >= 700 -> 3
        points >= 400 -> 2
        else -> 1
    }

    fun getLevelNameResId(): Int = when (level) {
        1 -> R.string.profile_level_1
        2 -> R.string.profile_level_2
        3 -> R.string.profile_level_3
        4 -> R.string.profile_level_4
        else -> R.string.profile_level_5
    }

    fun getLevelColor(): Color = when (level) {
        1 -> Color(0xFF9E9E9E)
        2 -> Color(0xFF4CAF50)
        3 -> Color(0xFF2196F3)
        4 -> Color(0xFF9C27B0)
        else -> Color(0xFFFFD700)
    }
    
    val isPenalized: Boolean 
        get() = System.currentTimeMillis() < penaltyEndTime
}
