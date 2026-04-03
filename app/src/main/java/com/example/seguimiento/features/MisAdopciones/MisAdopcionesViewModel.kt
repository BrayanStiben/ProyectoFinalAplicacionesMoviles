package com.example.seguimiento.features.MisAdopciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.AdoptionRequest
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.repositorios.AdoptionRepository
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class AdopcionConMascota(
    val request: AdoptionRequest,
    val mascota: Mascota?
)

data class MisAdopcionesUiState(
    val adopciones: List<AdopcionConMascota> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class MisAdopcionesViewModel @Inject constructor(
    private val adoptionRepository: AdoptionRepository,
    private val authRepository: AuthRepository,
    private val mascotaRepository: MascotaRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MisAdopcionesUiState> = authRepository.currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else {
                adoptionRepository.requests.combine(mascotaRepository.mascotas) { requests, mascotas ->
                    requests.filter { it.userId == user.id && it.status == AdoptionRequestStatus.APPROVED }
                        .map { req ->
                            val mascota = mascotas.find { it.id == req.mascotaId }
                            AdopcionConMascota(req, mascota)
                        }
                }
            }
        }
        .map { MisAdopcionesUiState(adopciones = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MisAdopcionesUiState(isLoading = true))
}
