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

    private val _uiState = MutableStateFlow(CertificadoRechazoUiState())
    val uiState: StateFlow<CertificadoRechazoUiState> = _uiState.asStateFlow()

    fun loadRequestData(requestId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val request = adoptionRepository.getById(requestId)
            val mascota = request?.let { mascotaRepository.getById(it.mascotaId) }
            val user = request?.let { userRepository.findById(it.userId) }
            val currentUser = authRepository.currentUser.value

            _uiState.update { it.copy(
                request = request, 
                mascota = mascota, 
                isLoading = false,
                isAdmin = currentUser?.role == UserRole.ADMIN,
                userRejectionCount = user?.rejectionCount ?: 0
            ) }
            
            if (user != null) {
                startPenaltyTimer(user.id)
            }
        }
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
        val request = _uiState.value.request ?: return
        val signatureBitmap = _uiState.value.adminSignature
        
        if (!_uiState.value.isAdmin) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            var encodedSignature: String? = null
            if (signatureBitmap != null) {
                val outputStream = ByteArrayOutputStream()
                signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                encodedSignature = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
            }

            if (encodedSignature != null) {
                adoptionRepository.saveAdminSignature(request.id, encodedSignature)
            }
            
            // Lógica de penalización en el USUARIO
            userRepository.incrementRejectionCount(request.userId)
            val updatedUser = userRepository.findById(request.userId)
            
            // Si llega a 3 rechazos, se le penaliza 1 hora (o el tiempo deseado)
            if (updatedUser != null && updatedUser.rejectionCount >= 3) {
                userRepository.applyPenalty(request.userId, 3600000) // 1 hora de penalización
                userRepository.resetRejectionCount(request.userId) // Reiniciamos para el futuro
            }
            
            adoptionRepository.updateRequestStatus(request.id, AdoptionRequestStatus.REJECTED)
            
            val updatedReq = adoptionRepository.getById(request.id)
            _uiState.update { it.copy(request = updatedReq, isLoading = false, isSuccess = true) }
        }
    }
}
