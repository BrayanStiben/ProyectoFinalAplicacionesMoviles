package com.example.seguimiento.Dominio.modelos

enum class UserRole {
    USER, MODERATOR, ADMIN
}

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val iconResId: Int
)

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
    val level: Int = 1,
    val badges: List<String> = emptyList(), // IDs of badges
    val lat: Double = 0.0,
    val lng: Double = 0.0
) {
    fun getLevelName(): String {
        return when (level) {
            1 -> "Novato"
            2 -> "Colaborador"
            3 -> "Protector"
            4 -> "Héroe"
            else -> "Leyenda"
        }
    }
}
