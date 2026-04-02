package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.AdoptionRequest
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import kotlinx.coroutines.flow.StateFlow

interface AdoptionRepository {
    val requests: StateFlow<List<AdoptionRequest>>
    suspend fun submitRequest(request: AdoptionRequest)
    suspend fun updateRequestStatus(requestId: String, status: AdoptionRequestStatus)
    suspend fun saveAdminSignature(requestId: String, signature: String?)
    suspend fun deleteRequest(requestId: String)
    fun getById(id: String): AdoptionRequest?
    fun getByUserId(userId: String): List<AdoptionRequest>
    suspend fun approveWithSignature(requestId: String, signature: String)
    suspend fun updatePenaltyInfo(requestId: String, rejectionCount: Int, penaltyEndTime: Long)
}
