package com.example.seguimiento.features.ConfirmacionMascotaFeliz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.repositorios.AdoptionRepository
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AdoptionViewModel @Inject constructor(
    private val adoptionRepository: AdoptionRepository,
    private val mascotaRepository: MascotaRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _requestId = MutableStateFlow<String?>(null)
    
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val uiState: StateFlow<AdoptionUiState> = _requestId.flatMapLatest { id ->
        if (id == null) flowOf(AdoptionUiState())
        else {
            val request = adoptionRepository.getById(id)
            if (request == null) flowOf(AdoptionUiState())
            else {
                val mascota = mascotaRepository.getById(request.mascotaId)
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                flowOf(AdoptionUiState(
                    petName = request.petName,
                    petType = request.petType,
                    petAge = request.petAge,
                    petImg = mascota?.imagenUrl ?: "",
                    petLoc = mascota?.ubicacion ?: "",
                    date = sdf.format(Date(request.timestamp)),
                    adoptedBy = request.userName,
                    motivation = request.motivation,
                    homeType = request.homeType,
                    hoursAlone = request.hoursAlone.toString(),
                    refName = request.referenceName,
                    refPhone = request.referencePhone
                ))
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdoptionUiState())

    fun setRequestId(id: String) {
        _requestId.value = id
    }
}
