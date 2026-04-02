package com.example.seguimiento.Dominio.modelos

enum class AdoptionRequestStatus {
    PENDING, APPROVED, REJECTED
}

data class AdoptionRequest(
    val id: String,
    val mascotaId: String,
    val userId: String,
    val userName: String,
    val userAddress: String = "",
    val userEmail: String = "",
    val petName: String,
    val petType: String,
    val petAge: String,
    val status: AdoptionRequestStatus = AdoptionRequestStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis(),
    // Datos del formulario
    val motivation: String,
    val expectations: String,
    val hoursAlone: Int,
    val homeType: String,
    val hasFencedYard: Boolean,
    val awareOfCosts: Boolean,
    val contingencyPlan: String,
    val movingPlan: String,
    val referenceName: String,
    val referencePhone: String,
    
    // Firma del solicitante (Usuario)
    val userSignature: String? = null,
    
    // Firma del administrador
    val adminSignature: String? = null,
    
    // Seguimiento de intentos y penalizaciones
    val rejectionCount: Int = 0,
    val penaltyEndTime: Long = 0
)
