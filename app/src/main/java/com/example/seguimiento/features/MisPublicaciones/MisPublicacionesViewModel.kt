package com.example.seguimiento.features.MisPublicaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MisPublicacionesUiState(
    val publicaciones: List<Mascota> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class MisPublicacionesViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val uiState: StateFlow<MisPublicacionesUiState> = authRepository.currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else {
                mascotaRepository.mascotas.map { lista ->
                    lista.filter { it.autorId == user.id }
                }
            }
        }
        .map { MisPublicacionesUiState(publicaciones = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MisPublicacionesUiState(isLoading = true))

    fun resolverPublicacion(id: String) {
        mascotaRepository.actualizarEstado(id, PublicacionEstado.RESUELTA)
    }

    fun eliminarPublicacion(id: String) {
        mascotaRepository.delete(id)
    }
}
