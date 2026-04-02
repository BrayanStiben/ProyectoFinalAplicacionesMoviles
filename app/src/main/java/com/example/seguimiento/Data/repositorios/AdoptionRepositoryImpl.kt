package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.AdoptionRequest
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import com.example.seguimiento.Dominio.repositorios.AdoptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdoptionRepositoryImpl @Inject constructor() : AdoptionRepository {
    private val _requests = MutableStateFlow<List<AdoptionRequest>>(emptyList())
    override val requests: StateFlow<List<AdoptionRequest>> = _requests.asStateFlow()

    override suspend fun submitRequest(request: AdoptionRequest) {
        _requests.update { it + request }
    }

    override suspend fun updateRequestStatus(requestId: String, status: AdoptionRequestStatus) {
        _requests.update { list ->
            list.map { if (it.id == requestId) it.copy(status = status) else it }
        }
    }

    override suspend fun saveAdminSignature(requestId: String, signature: String?) {
        _requests.update { list ->
            list.map { if (it.id == requestId) it.copy(adminSignature = signature) else it }
        }
    }

    override suspend fun approveWithSignature(requestId: String, signature: String) {
        _requests.update { list ->
            list.map { 
                if (it.id == requestId) {
                    it.copy(status = AdoptionRequestStatus.APPROVED, adminSignature = signature)
                } else it 
            }
        }
    }

    override suspend fun updatePenaltyInfo(requestId: String, rejectionCount: Int, penaltyEndTime: Long) {
        _requests.update { list ->
            list.map { 
                if (it.id == requestId) {
                    it.copy(rejectionCount = rejectionCount, penaltyEndTime = penaltyEndTime)
                } else it 
            }
        }
    }

    override suspend fun deleteRequest(requestId: String) {
        _requests.update { list -> list.filter { it.id != requestId } }
    }

    override fun getById(id: String): AdoptionRequest? {
        return _requests.value.find { it.id == id }
    }

    override fun getByUserId(userId: String): List<AdoptionRequest> {
        return _requests.value.filter { it.userId == userId }
    }
}
