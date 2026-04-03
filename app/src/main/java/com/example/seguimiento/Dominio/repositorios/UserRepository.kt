package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.Dominio.modelos.UsuarioEstadisticas
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val users: StateFlow<List<User>>
    fun save(user: User)
    fun findById(id: String): User?
    fun findByEmail(email: String): User?
    fun updatePassword(email: String, newPassword: String): Boolean
    fun login(email: String, password: String): User?
    fun getUsuariosConEstadisticas(): List<UsuarioEstadisticas>
    fun deleteAccount(id: String)
    
    // Nuevos métodos para penalizaciones y sistema de logros
    suspend fun incrementRejectionCount(userId: String)
    suspend fun resetRejectionCount(userId: String)
    suspend fun applyPenalty(userId: String, durationMillis: Long)
    
    // Sistema de Logros y Puntos
    suspend fun addPoints(userId: String, points: Int)
    suspend fun addBadge(userId: String, badgeId: String)
}
