package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.Dominio.modelos.UserRole
import com.example.seguimiento.Dominio.modelos.UsuarioEstadisticas
import com.example.seguimiento.Dominio.repositorios.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    override val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        _users.value = fetchUsers()
    }

    override fun save(user: User) {
        _users.update { currentList ->
            val index = currentList.indexOfFirst { it.id == user.id || it.email == user.email }
            if (index != -1) {
                currentList.toMutableList().apply { set(index, user) }
            } else {
                currentList + user
            }
        }
    }

    override fun findById(id: String): User? {
        return _users.value.firstOrNull { it.id == id }
    }

    override fun findByEmail(email: String): User? {
        return _users.value.firstOrNull { it.email == email }
    }

    override fun updatePassword(email: String, newPassword: String): Boolean {
        var updated = false
        _users.update { currentList ->
            currentList.map { 
                if (it.email == email) {
                    updated = true
                    it.copy(password = newPassword)
                } else it 
            }
        }
        return updated
    }

    override fun login(email: String, password: String): User? {
        return _users.value.firstOrNull { it.email == email && it.password == password }
    }

    override fun getUsuariosConEstadisticas(): List<UsuarioEstadisticas> {
        return _users.value.map { UsuarioEstadisticas(it.name, it.departamento, it.city) }
    }

    override fun deleteAccount(id: String) {
        _users.update { it.filter { user -> user.id != id } }
    }

    override suspend fun incrementRejectionCount(userId: String) {
        _users.update { list ->
            list.map { user ->
                if (user.id == userId) {
                    val newCount = user.rejectionCount + 1
                    user.copy(rejectionCount = newCount)
                } else user
            }
        }
    }

    override suspend fun resetRejectionCount(userId: String) {
        _users.update { list ->
            list.map { user ->
                // NOTA: Solo reseteamos el contador, NO la penalización.
                // La penalización se limpia sola cuando pasa el tiempo (ahora < penaltyEndTime)
                if (user.id == userId) user.copy(rejectionCount = 0) else user
            }
        }
    }

    override suspend fun applyPenalty(userId: String, durationMillis: Long) {
        _users.update { list ->
            list.map { user ->
                if (user.id == userId) {
                    user.copy(penaltyEndTime = System.currentTimeMillis() + durationMillis)
                } else user
            }
        }
    }

    private fun fetchUsers(): List<User> {
        return listOf(

            User(
                id = "3",
                name = "Administrador",
                city = "Armenia",
                departamento = "Quindío",
                address = "Calle Admin",
                email = "admin@gmail.com",
                password = "admin",
                profilePictureUrl = "https://picsum.photos/200?random=3",
                role = UserRole.ADMIN
            )
        )
    }
}
