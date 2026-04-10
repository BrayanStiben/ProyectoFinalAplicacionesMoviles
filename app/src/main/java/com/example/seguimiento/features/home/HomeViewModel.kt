package com.example.seguimiento.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Logro
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.Notificacion
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.LogrosRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val mascotaRepository: MascotaRepository,
    private val notificacionRepository: NotificacionRepository,
    private val logrosRepository: LogrosRepository
) : ViewModel() {

    val currentUser = authRepository.currentUser

    val userName: StateFlow<String> = authRepository.currentUser
        .map { user -> user?.name ?: "Usuario" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Usuario")

    val userProfilePicture: StateFlow<String?> = authRepository.currentUser
        .map { user -> user?.profilePictureUrl }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val notificaciones: StateFlow<List<Notificacion>> = notificacionRepository.notificaciones

    val todosLosLogros: List<Logro> = logrosRepository.todosLosLogros
    
    val logrosObtenidos: StateFlow<List<String>> = authRepository.currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else logrosRepository.getLogrosUsuario(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _filtroCategoria = MutableStateFlow<String?>(null)
    val filtroCategoria = _filtroCategoria.asStateFlow()

    val mascotasFeed: StateFlow<List<Mascota>> = combine(
        mascotaRepository.mascotas,
        _filtroCategoria
    ) { lista, categoria ->
        lista.filter { mascota ->
            val esVisible = mascota.estado == PublicacionEstado.VERIFICADA || 
                          mascota.estado == PublicacionEstado.RESUELTA ||
                          mascota.estado == PublicacionEstado.ADOPTADA
            val coincideCategoria = categoria == null || mascota.tipo.equals(categoria, ignoreCase = true)
            esVisible && coincideCategoria
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mascotasRecomendadas: StateFlow<List<Mascota>> = mascotaRepository.mascotas
        .map { lista -> lista.filter { it.esDestacada } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedNavItem = MutableStateFlow(0)
    val selectedNavItem = _selectedNavItem.asStateFlow()

    fun onNavItemClicked(index: Int) {
        _selectedNavItem.value = index
    }

    fun filtrarPorCategoria(categoria: String?) {
        _filtroCategoria.value = categoria
    }

    fun asegurarMascotaEnRepo(mascota: Mascota) {
        if (mascotaRepository.getById(mascota.id) == null) {
            mascotaRepository.save(mascota)
        }
    }

    fun toggleLike(id: String) {
        val userId = currentUser.value?.id ?: return
        val mascota = mascotaRepository.getById(id)
        
        mascotaRepository.toggleLike(id, userId)
        
        // Notificar al autor de la mascota (si no es el mismo usuario)
        if (mascota != null && mascota.autorId != userId && mascota.autorId.isNotEmpty()) {
            notificacionRepository.addNotificacion(
                tituloResId = R.string.home_notif_like_title,
                mensajeResId = R.string.home_notif_like_msg,
                mensajeArgs = listOf(mascota.nombre),
                tipo = "POST_VOTADO",
                userId = mascota.autorId
            )
        }
        
        // Lógica de logro: 10 likes
        viewModelScope.launch {
            val allMascotas = mascotaRepository.mascotas.value
            val misLikes = allMascotas.count { it.likerIds.contains(userId) }
            if (misLikes >= 10) {
                logrosRepository.ganarLogro(userId, "com_heart")
            }
        }
    }
}
