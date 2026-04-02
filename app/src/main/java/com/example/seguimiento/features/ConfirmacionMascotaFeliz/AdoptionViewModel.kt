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
                    summary = """
                        Motivación: ${request.motivation}
                        Vivienda: ${request.homeType}
                        Horas solo: ${request.hoursAlone}
                        Referencia: ${request.referenceName} (${request.referencePhone})
                    """.trimIndent(),
                    adminComment = "La adopción de la mascota ${request.petName}, de tipo ${request.petType}, ha sido registrada exitosamente a nombre de ${request.userName}. La información ha sido almacenada correctamente en el sistema y el estado de la mascota ha sido actualizado como adoptada. Gracias por completar el proceso de adopción en PetAdopt."
                ))
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdoptionUiState())

    fun setRequestId(id: String) {
        _requestId.value = id
    }

    fun onHistoryClick() {
        // Implementar si se desea navegar a un detalle histórico
    }
}
