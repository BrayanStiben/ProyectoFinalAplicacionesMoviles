package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.AdoptionRequest
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import com.example.seguimiento.Dominio.repositorios.AdoptionRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.example.seguimiento.R

@Singleton
class AdoptionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val notificacionRepository: NotificacionRepository
) : AdoptionRepository {

    private val collection = firestore.collection("solicitudes_adopcion")
    private val _requests = MutableStateFlow<List<AdoptionRequest>>(emptyList())
    override val requests: StateFlow<List<AdoptionRequest>> = _requests.asStateFlow()

    init {
        collection.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(AdoptionRequest::class.java)
                    _notificaciones_update(list)
                }
            }
    }

    private fun _notificaciones_update(list: List<AdoptionRequest>) {
        _requests.value = list
        if (list.isEmpty()) seedInitialRequests()
    }

    private fun seedInitialRequests() {
        val initialRequest = AdoptionRequest(
            id = "req_001",
            mascotaId = "pet_001",
            userId = "user_test",
            userName = "Usuario Prueba",
            petName = "Firulais",
            petType = "Perro",
            petAge = "2 años",
            status = AdoptionRequestStatus.PENDING,
            motivation = "Quiero darle un hogar lleno de amor."
        )
        collection.document(initialRequest.id).set(initialRequest)
    }

    override suspend fun submitRequest(request: AdoptionRequest) {
        val id = if (request.id.isEmpty()) collection.document().id else request.id
        collection.document(id).set(request.copy(id = id)).await()
        
        notificacionRepository.addNotificacion(
            tituloResId = R.string.notif_adoption_sent_title,
            mensajeResId = R.string.notif_adoption_sent_msg,
            mensajeArgs = listOf(request.petName),
            tipo = "INFO",
            userId = request.userId
        )
    }

    override suspend fun updateRequestStatus(requestId: String, status: AdoptionRequestStatus) {
        val request = getById(requestId)
        collection.document(requestId).update("status", status).await()
        
        request?.let {
            val (titRes, msjRes, tipo) = when(status) {
                AdoptionRequestStatus.APPROVED -> Triple(R.string.notif_adoption_approved_title, R.string.notif_adoption_approved_msg, "SUCCESS")
                AdoptionRequestStatus.REJECTED -> Triple(R.string.notif_adoption_rejected_title, R.string.notif_adoption_rejected_msg, "ERROR")
                else -> Triple(R.string.notif_adoption_update_title, R.string.notif_adoption_update_msg, "INFO")
            }
            notificacionRepository.addNotificacion(
                tituloResId = titRes,
                mensajeResId = msjRes,
                mensajeArgs = listOf(it.petName),
                tipo = tipo,
                userId = it.userId
            )
        }
    }

    override suspend fun saveAdminSignature(requestId: String, signature: String?) {
        collection.document(requestId).update("adminSignature", signature).await()
    }

    override suspend fun approveWithSignature(requestId: String, signature: String) {
        val request = getById(requestId)
        collection.document(requestId).update("status", AdoptionRequestStatus.APPROVED, "adminSignature", signature).await()
        request?.let {
            notificacionRepository.addNotificacion(
                tituloResId = R.string.notif_adoption_approved_title,
                mensajeResId = R.string.notif_adoption_signed_msg,
                tipo = "SUCCESS",
                userId = it.userId
            )
        }
    }

    override suspend fun updatePenaltyInfo(requestId: String, rejectionCount: Int, penaltyEndTime: Long) {
        collection.document(requestId).update("rejectionCount", rejectionCount, "penaltyEndTime", penaltyEndTime).await()
    }

    override suspend fun deleteRequest(requestId: String) {
        collection.document(requestId).delete().await()
    }

    override fun getById(id: String): AdoptionRequest? = _requests.value.find { it.id == id }
    override fun getByUserId(userId: String): List<AdoptionRequest> = _requests.value.filter { it.userId == userId }
}
