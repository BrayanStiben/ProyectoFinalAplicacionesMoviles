package com.example.seguimiento.features.Favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FavoritosViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser = authRepository.currentUser

    val mascotasFavoritas: StateFlow<List<Mascota>> = combine(
        mascotaRepository.mascotas,
        authRepository.currentUser
    ) { mascotas, user ->
        if (user == null) emptyList()
        else mascotas.filter { it.likerIds.contains(user.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleLike(mascotaId: String) {
        val userId = currentUser.value?.id ?: return
        mascotaRepository.toggleLike(mascotaId, userId)
    }
}
