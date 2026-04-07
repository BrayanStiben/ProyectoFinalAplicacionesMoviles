package com.example.seguimiento.features.CertificadoRechazo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.AdoptionRequest
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.UserRole
import com.example.seguimiento.Dominio.repositorios.AdoptionRepository
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject

data class CertificadoRechazoUiState(
    val request: AdoptionRequest? = null,
    val mascota: Mascota? = null,
    val isLoading: Boolean = false,
    val isPenalized: Boolean = false,
    val penaltyTimeLeft: String = "",
    val adminSignature: Bitmap? = null,
    val isSuccess: Boolean = false,
    val isAdmin: Boolean = false,
    val userRejectionCount: Int = 0
)

@HiltViewModel
class CertificadoRechazoViewModel @Inject constructor(
    private val adoptionRepository: AdoptionRepository,
    private val mascotaRepository: MascotaRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CertificadoRechazoUiState(
        isAdmin = authRepository.currentUser.value?.role == UserRole.ADMIN
    ))
    val uiState: StateFlow<CertificadoRechazoUiState> = _uiState.asStateFlow()

    fun loadRequestData(requestId: String) {
        // 1. Carga de datos de la solicitud y usuario
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val request = adoptionRepository.getById(requestId)
            val mascota = request?.let { mascotaRepository.getById(it.mascotaId) }
            val user = request?.let { userRepository.findById(it.userId) }

            _uiState.update { it.copy(
                request = request, 
                mascota = mascota, 
                isLoading = false,
                userRejectionCount = user?.rejectionCount ?: 0
            ) }
            
            if (user != null) {
                startPenaltyTimer(user.id)
            }
        }

        // 2. Observación reactiva y continua del rol de Admin
        authRepository.currentUser
            .onEach { currentUser ->
                _uiState.update { it.copy(isAdmin = currentUser?.role == UserRole.ADMIN) }
            }
            .launchIn(viewModelScope)
    }

    private fun startPenaltyTimer(userId: String) {
        viewModelScope.launch {
            while (true) {
                val user = userRepository.findById(userId) ?: break
                val now = System.currentTimeMillis()
                val timeLeft = user.penaltyEndTime - now
                
                if (timeLeft > 0) {
                    val seconds = (timeLeft / 1000) % 60
                    val minutes = (timeLeft / (1000 * 60)) % 60
                    _uiState.update { 
                        it.copy(
                            isPenalized = true,
                            penaltyTimeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                        ) 
                    }
                } else {
                    _uiState.update { it.copy(isPenalized = false, penaltyTimeLeft = "") }
                }
                delay(1000)
            }
        }
    }

    fun setSignature(bitmap: Bitmap) {
        _uiState.update { it.copy(adminSignature = bitmap) }
    }

    fun finalizeRejection() {
        val uiStateValue = _uiState.value
        val request = uiStateValue.request ?: return
        val signatureBitmap = uiStateValue.adminSignature
        
        if (!uiStateValue.isAdmin) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            var encodedSignature: String? = null
            if (signatureBitmap != null) {
                val outputStream = ByteArrayOutputStream()
                signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                encodedSignature = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
            }

            // 1. Guardar firma si existe
            if (encodedSignature != null) {
                adoptionRepository.saveAdminSignature(request.id, encodedSignature)
            }
            
            // 2. Lógica de penalización en el USUARIO
            userRepository.incrementRejectionCount(request.userId)
            val updatedUser = userRepository.findById(request.userId)
            
            // 3. Determinar si se aplica penalización
            if (updatedUser != null && updatedUser.rejectionCount >= 3) {
                userRepository.applyPenalty(request.userId, 3600000) // 1 hora de penalización
                val userWithPenalty = userRepository.findById(request.userId)
                
                // Guardamos el 3/3 y el tiempo final en la solicitud
                adoptionRepository.updatePenaltyInfo(
                    request.id, 
                    3, 
                    userWithPenalty?.penaltyEndTime ?: (System.currentTimeMillis() + 3600000)
                )
                userRepository.resetRejectionCount(request.userId)
            } else {
                // Guardar el conteo actual (1 o 2) en la solicitud
                adoptionRepository.updatePenaltyInfo(request.id, updatedUser?.rejectionCount ?: 0, 0L)
            }
            
            // 4. Actualizar estado de la solicitud
            adoptionRepository.updateRequestStatus(request.id, AdoptionRequestStatus.REJECTED)
            
            // 5. Actualizar UI
            val updatedReq = adoptionRepository.getById(request.id)
            _uiState.update { it.copy(request = updatedReq, isLoading = false, isSuccess = true) }
        }
    }
}
