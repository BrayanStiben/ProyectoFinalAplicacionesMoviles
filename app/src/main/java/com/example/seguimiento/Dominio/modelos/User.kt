package com.example.seguimiento.Dominio.modelos

enum class UserRole {
    USER, ADMIN
}

data class User(
    val id: String,
    val name: String,
    val city: String,
    val address: String,
    val email: String,
    val password: String,
    val profilePictureUrl: String,
    val role: UserRole = UserRole.USER
)
