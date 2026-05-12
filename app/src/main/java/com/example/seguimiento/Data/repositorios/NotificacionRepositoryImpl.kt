package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Notificacion
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.core.utils.ResourceProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificacionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val resourceProvider: ResourceProvider
) : NotificacionRepository {

    private val collection = firestore.collection("notificaciones")
    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
    override val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

    init {
        collection.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(Notificacion::class.java)
                    _notificaciones.value = list
                    if (list.isEmpty()) seedInitialNotif()
                }
            }
    }

    private fun seedInitialNotif() {
        val welcome = Notificacion(
            id = "welcome_notif",
            titulo = "¡Bienvenido!",
            mensaje = "Gracias por unirte a Seguimiento App.",
            tipo = "INFO",
            userId = "admin_id",
            leida = false
        )
        collection.document(welcome.id).set(welcome)
    }

    override suspend fun addNotificacion(
        tituloResId: Int,
        mensajeResId: Int,
        mensajeArgs: List<Any>,
        tipo: String,
        userId: String?
    ) {
        val titulo = resourceProvider.getString(tituloResId)
        val mensaje = resourceProvider.getString(mensajeResId, *mensajeArgs.toTypedArray())
        val id = collection.document().id
        val notif = Notificacion(
            id = id,
            titulo = titulo,
            mensaje = mensaje,
            tipo = tipo,
            timestamp = System.currentTimeMillis(),
            userId = userId ?: "",
            leida = false
        )
        collection.document(id).set(notif)
    }

    override suspend fun deleteNotificacion(id: String) {
        collection.document(id).delete()
    }

    override suspend fun clearAll(userId: String) {
        val batch = firestore.batch()
        _notificaciones.value.filter { it.userId == userId }.forEach {
            batch.delete(collection.document(it.id))
        }
        batch.commit()
    }

    override suspend fun marcarComoLeida(id: String) {
        collection.document(id).update("leida", true)
    }
}
