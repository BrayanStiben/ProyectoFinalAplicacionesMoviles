package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<User?>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(user: User): Result<Unit>
    suspend fun logout()
    suspend fun recoverPassword(email: String): Result<Unit>
}
