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

    override suspend fun addPoints(userId: String, points: Int) {
        _users.update { list ->
            list.map { user ->
                if (user.id == userId) {
                    user.copy(points = user.points + points)
                } else user
            }
        }
    }

    override suspend fun addBadge(userId: String, badgeId: String) {
        _users.update { list ->
            list.map { user ->
                if (user.id == userId && !user.badges.contains(badgeId)) {
                    user.copy(badges = user.badges + badgeId)
                } else user
            }
        }
    }

    private fun fetchUsers(): List<User> {
        return listOf(
            User(
                id = "user_colaborador",
                name = "Carlos Colaborador",
                city = "Bogotá",
                departamento = "Cundinamarca",
                address = "Calle 10",
                email = "colaborador@gmail.com",
                password = "123",
                profilePictureUrl = "https://picsum.photos/200?random=10",
                role = UserRole.USER,
                points = 450 
            ),
            User(
                id = "user_protector",
                name = "Patricia Protectora",
                city = "Medellín",
                departamento = "Antioquia",
                address = "Carrera 20",
                email = "protector@gmail.com",
                password = "123",
                profilePictureUrl = "https://picsum.photos/200?random=11",
                role = UserRole.USER,
                points = 850 
            ),
            User(
                id = "user_heroe",
                name = "Humberto Héroe",
                city = "Cali",
                departamento = "Valle del Cauca",
                address = "Avenida 30",
                email = "heroe@gmail.com",
                password = "123",
                profilePictureUrl = "https://picsum.photos/200?random=12",
                role = UserRole.USER,
                points = 1200 
            ),
            User(
                id = "user_leyenda",
                name = "Luis Leyenda",
                city = "Barranquilla",
                departamento = "Atlántico",
                address = "Calle 40",
                email = "leyenda@gmail.com",
                password = "123",
                profilePictureUrl = "https://picsum.photos/200?random=13",
                role = UserRole.USER,
                points = 2000 
            ),
            User(
                id = "admin_id",
                name = "Administrador",
                city = "Armenia",
                departamento = "Quindío",
                address = "Oficina Central",
                email = "admin@gmail.com",
                password = "admin",
                profilePictureUrl = "https://picsum.photos/200?random=1",
                role = UserRole.ADMIN,
                points = 5000
            )
        )
    }
}
