package com.example.seguimiento.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.Notificacion
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val mascotaRepository: MascotaRepository
) : ViewModel() {

    val currentUser = authRepository.currentUser

    val userName: StateFlow<String> = authRepository.currentUser
        .map { user -> user?.name ?: "Usuario" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Usuario")

    val userProfilePicture: StateFlow<String?> = authRepository.currentUser
        .map { user -> user?.profilePictureUrl }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _notificaciones = MutableStateFlow<List<Notificacion>>(listOf(
        Notificacion("1", "¡Bienvenido!", "Gracias por unirte a PetAdopt."),
        Notificacion("2", "Nueva mascota cerca", "Un nuevo perrito ha sido publicado en tu zona.", tipo = "ZONA_NUEVA")
    ))
    val notificaciones = _notificaciones.asStateFlow()

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

    fun toggleLike(id: String) {
        val userId = currentUser.value?.id ?: return
        mascotaRepository.toggleLike(id, userId)
    }
}
