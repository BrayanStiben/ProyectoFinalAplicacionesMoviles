package com.example.seguimiento.Dominio.repositorios

import android.net.Uri
import com.example.seguimiento.Dominio.modelos.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<User?>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(user: User, password: String, imageUri: Uri? = null): Result<Unit>
    suspend fun updateProfile(user: User, imageUri: Uri? = null): Result<Unit>
    suspend fun logout()
    suspend fun recoverPassword(email: String): Result<Unit>
    
    // Métodos para el código de verificación random
    fun generateVerificationCode(): String
    fun getVerificationCode(): String?
}
