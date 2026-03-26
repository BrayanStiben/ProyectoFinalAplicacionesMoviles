package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val users: StateFlow<List<User>>
    fun save(user: User)
    fun findById(id: String): User?
    fun login(email: String, password: String): User?
}
