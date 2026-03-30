package com.example.seguimiento.features.MiPerfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val activePosts: Int = 0,
    val resolvedPosts: Int = 0,
    val pendingPosts: Int = 0,
    val userPosts: List<Mascota> = emptyList()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val mascotaRepository: MascotaRepository
) : ViewModel() {

    // En una app real, obtendríamos el ID del usuario logueado desde un AuthManager
    private val currentUserId = "1" 

    val uiState: StateFlow<ProfileUiState> = combine(
        userRepository.users,
        mascotaRepository.mascotas
    ) { users, mascotas ->
        val user = users.find { it.id == currentUserId }
        val userPosts = mascotas.filter { it.autorId == currentUserId }
        
        ProfileUiState(
            user = user,
            activePosts = userPosts.count { it.estado == PublicacionEstado.VERIFICADA },
            resolvedPosts = userPosts.count { it.estado == PublicacionEstado.RESUELTA },
            pendingPosts = userPosts.count { it.estado == PublicacionEstado.PENDIENTE },
            userPosts = userPosts
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState())

    fun resolvePost(postId: String) {
        mascotaRepository.actualizarEstado(postId, PublicacionEstado.RESUELTA)
    }

    fun deletePost(postId: String) {
        mascotaRepository.delete(postId)
    }
}
