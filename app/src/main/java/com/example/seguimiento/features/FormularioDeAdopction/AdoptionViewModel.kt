package com.example.seguimiento.features.FormularioDeAdopction

import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.AdoptionRequest
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import com.example.seguimiento.Dominio.repositorios.AdoptionRepository
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AdoptionViewModel @Inject constructor(
    private val adoptionRepository: AdoptionRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    // Estado único para todo el formulario compartido entre pasos
    var state by mutableStateOf(AdoptionFormState())
        private set

    fun updateState(newState: AdoptionFormState) {
        state = newState
    }

    fun setPetInfo(id: String, name: String, type: String, age: String) {
        state = state.copy(
            petId = id,
            petName = name,
            petType = type,
            petAge = age
        )
    }

    fun submitForm(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val authUser = authRepository.currentUser.value
            val user = authUser?.let { userRepository.findById(it.id) }
            
            // Convertimos la firma del usuario a Base64 si existe
            var encodedUserSignature: String? = null
            state.signatureBitmap?.let { bitmap ->
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                encodedUserSignature = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
            }

            val request = AdoptionRequest(
                id = UUID.randomUUID().toString(),
                mascotaId = state.petId,
                userId = user?.id ?: "anonimo",
                userName = user?.name ?: "Usuario",
                userEmail = user?.email ?: "",
                userAddress = user?.address ?: "",
                petName = state.petName,
                petType = state.petType,
                petAge = state.petAge,
                status = AdoptionRequestStatus.PENDING,
                motivation = state.motivation,
                expectations = state.expectations,
                hoursAlone = state.hoursAlone,
                homeType = state.homeType,
                hasFencedYard = state.hasFencedYard,
                awareOfCosts = state.awareOfCosts,
                contingencyPlan = state.contingencyPlan,
                movingPlan = state.movingPlan,
                referenceName = state.referenceName,
                referencePhone = state.referencePhone,
                userSignature = encodedUserSignature,
                // Pasamos la información de penalización actual del usuario a la solicitud
                rejectionCount = user?.rejectionCount ?: 0,
                penaltyEndTime = user?.penaltyEndTime ?: 0L
            )
            adoptionRepository.submitRequest(request)
            
            // Limpiar el estado para el siguiente uso
            state = AdoptionFormState()
            
            onSuccess()
        }
    }
}
