package com.example.seguimiento.features.CertificadoAdopcion

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.AdoptionRequest
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.modelos.UserRole
import com.example.seguimiento.Dominio.repositorios.AdoptionRepository
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

data class CertificadoUiState(
    val request: AdoptionRequest? = null,
    val mascota: com.example.seguimiento.Dominio.modelos.Mascota? = null,
    val isLoading: Boolean = false,
    val adminSignature: Bitmap? = null,
    val isSuccess: Boolean = false,
    val isAdmin: Boolean = false
)

@HiltViewModel
class CertificadoAdopcionViewModel @Inject constructor(
    private val adoptionRepository: AdoptionRepository,
    private val mascotaRepository: MascotaRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _requestId = MutableStateFlow<String?>(null)
    private val _adminSignature = MutableStateFlow<Bitmap?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isSuccess = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CertificadoUiState> = combine(
        _requestId.flatMapLatest { id ->
            if (id == null) flowOf(null)
            else adoptionRepository.requests.map { list -> list.find { it.id == id } }
        },
        _adminSignature,
        _isLoading,
        _isSuccess,
        authRepository.currentUser
    ) { request, signature, loading, success, currentUser ->
        val mascota = if (request != null) mascotaRepository.getById(request.mascotaId) else null
        CertificadoUiState(
            request = request,
            mascota = mascota,
            isLoading = loading,
            adminSignature = signature,
            isSuccess = success,
            isAdmin = currentUser?.role == UserRole.ADMIN
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CertificadoUiState())

    fun loadRequestData(requestId: String) {
        _requestId.value = requestId
    }

    fun setSignature(bitmap: Bitmap) {
        _adminSignature.value = bitmap
    }

    fun finalizeAdoption() {
        val request = uiState.value.request ?: return
        val signatureBitmap = _adminSignature.value
        
        if (!uiState.value.isAdmin) return // Seguridad básica

        viewModelScope.launch {
            _isLoading.value = true
            
            var encodedSignature: String? = null
            if (signatureBitmap != null) {
                val outputStream = ByteArrayOutputStream()
                signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                val byteArray = outputStream.toByteArray()
                encodedSignature = Base64.encodeToString(byteArray, Base64.DEFAULT)
            }
            
            if (encodedSignature != null) {
                adoptionRepository.approveWithSignature(request.id, encodedSignature)
                mascotaRepository.actualizarEstado(request.mascotaId, PublicacionEstado.ADOPTADA)
                _isSuccess.value = true
            }
            
            _isLoading.value = false
        }
    }
}
