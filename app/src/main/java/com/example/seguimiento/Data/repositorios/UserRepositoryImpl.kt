package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.Dominio.modelos.UserRole
import com.example.seguimiento.Dominio.modelos.UsuarioEstadisticas
import com.example.seguimiento.Dominio.repositorios.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        val currentList = _users.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == user.id || it.email == user.email }
        
        if (index != -1) {
            currentList[index] = user
        } else {
            currentList.add(user)
        }
        _users.value = currentList
    }

    override fun findById(id: String): User? {
        return _users.value.firstOrNull { it.id == id }
    }

    override fun login(email: String, password: String): User? {
        return _users.value.firstOrNull { it.email == email && it.password == password }
    }

    override fun getUsuariosConEstadisticas(): List<UsuarioEstadisticas> {
        return _users.value.map { UsuarioEstadisticas(it.name, it.departamento, it.city) }
    }

    override fun deleteAccount(id: String) {
        _users.value = _users.value.filter { it.id != id }
    }

    private fun fetchUsers(): List<User> {
        return listOf(
            User(
                id = "1",
                name = "Juan",
                city = "Bogotá", 
                departamento = "Cundinamarca",
                address = "Calle 123",
                email = "juan@email.com",
                password = "111",
                profilePictureUrl = "https://m.media-amazon.com/images/I/41g6jROgo0L.png",
                role = UserRole.USER,
                points = 150,
                level = 2
            ),
            User(
                id = "2",
                name = "Moderador",
                city = "Medellín",
                departamento = "Antioquia",
                address = "Calle 456",
                email = "mod@gmail.com",
                password = "mod",
                profilePictureUrl = "https://picsum.photos/200?random=2",
                role = UserRole.MODERATOR
            ),
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
