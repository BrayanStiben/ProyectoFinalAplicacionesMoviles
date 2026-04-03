package com.example.seguimiento.features.GestionAdopciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.AdoptionRequest
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.AdoptionRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestionAdopcionesViewModel @Inject constructor(
    private val adoptionRepository: AdoptionRepository,
    private val mascotaRepository: MascotaRepository,
    private val userRepository: UserRepository,
    private val notificacionRepository: NotificacionRepository
) : ViewModel() {

    val requests: StateFlow<List<AdoptionRequest>> = adoptionRepository.requests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun acceptAdoption(request: AdoptionRequest) {
        viewModelScope.launch {
            adoptionRepository.updateRequestStatus(request.id, AdoptionRequestStatus.APPROVED)
            mascotaRepository.actualizarEstado(request.mascotaId, PublicacionEstado.ADOPTADA)
            userRepository.resetRejectionCount(request.userId)

            // NOTIFICACIÓN: Adopción aprobada
            notificacionRepository.addNotificacion(
                titulo = "¡Adopción Aprobada! 🐾🎉",
                mensaje = "¡Felicidades! Tu solicitud para adoptar a ${request.petName} ha sido aprobada. Revisa tu correo para los siguientes pasos.",
                tipo = "INFO",
                userId = request.userId
            )
        }
    }

    fun rejectAdoption(request: AdoptionRequest) {
        viewModelScope.launch {
            adoptionRepository.updateRequestStatus(request.id, AdoptionRequestStatus.REJECTED)
            
            // Incrementar contador de rechazos en el usuario
            userRepository.incrementRejectionCount(request.userId)
            
            val user = userRepository.findById(request.userId)
            if (user != null) {
                // NOTIFICACIÓN: Adopción rechazada
                notificacionRepository.addNotificacion(
                    titulo = "Solicitud de Adopción ❌",
                    mensaje = "Lo sentimos, tu solicitud para adoptar a ${request.petName} no ha sido aprobada en esta ocasión.",
                    tipo = "INFO",
                    userId = request.userId
                )

                // Si llegamos a 3 o más intentos
                if (user.rejectionCount >= 3) {
                    // Aplicar penalización de 1 minuto
                    userRepository.applyPenalty(request.userId, 60000)
                    
                    val userWithPenalty = userRepository.findById(request.userId)
                    // Actualizar LA SOLICITUD con 3/3 para que el admin lo vea antes de que se limpie el perfil
                    adoptionRepository.updatePenaltyInfo(
                        request.id, 
                        3, 
                        userWithPenalty?.penaltyEndTime ?: 0L
                    )

                    // NOTIFICACIÓN: Penalización
                    notificacionRepository.addNotificacion(
                        titulo = "Cuenta Suspendida Temporalmente ⚠️",
                        mensaje = "Has alcanzado el límite de rechazos. Tu cuenta ha sido suspendida por 1 minuto.",
                        tipo = "INFO",
                        userId = request.userId
                    )
                    
                    // Resetear contador del PERFIL para la próxima vez que intente (después de la penalización)
                    userRepository.resetRejectionCount(request.userId)
                } else {
                    // Solo actualizar el conteo en la solicitud
                    adoptionRepository.updatePenaltyInfo(request.id, user.rejectionCount, 0L)
                }
            }
        }
    }
}
