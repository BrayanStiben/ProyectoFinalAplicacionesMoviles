package com.example.seguimiento.features.GestionUsuarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.Dominio.repositorios.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GestionUsuariosUiState(
    val usuariosTotales: Int = 0,
    val usuariosBaneados: Int = 0,
    val listaUsuarios: List<User> = emptyList(),
    val listaUsuariosBaneados: List<User> = emptyList()
)

@HiltViewModel
class GestionUsuariosViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val uiState: StateFlow<GestionUsuariosUiState> = userRepository.users.map { users ->
        val activos = users.filter { !it.isBanned }
        val baneados = users.filter { it.isBanned }
        GestionUsuariosUiState(
            usuariosTotales = activos.size,
            usuariosBaneados = baneados.size,
            listaUsuarios = activos,
            listaUsuariosBaneados = baneados
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GestionUsuariosUiState()
    )

    fun banearUsuario(userId: String, motivo: String) {
        viewModelScope.launch {
            val user = userRepository.findById(userId)
            if (user != null) {
                userRepository.save(user.copy(isBanned = true, banReason = motivo))
            }
        }
    }

    fun desbanearUsuario(userId: String) {
        viewModelScope.launch {
            val user = userRepository.findById(userId)
            if (user != null) {
                userRepository.save(user.copy(isBanned = false, banReason = ""))
            }
        }
    }
}
