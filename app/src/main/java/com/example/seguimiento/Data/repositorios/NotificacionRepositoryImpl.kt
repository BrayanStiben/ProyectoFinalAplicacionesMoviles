package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Notificacion
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.core.utils.ResourceProvider
import com.example.seguimiento.core.utils.SessionManager
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
    private val resourceProvider: ResourceProvider,
    private val sessionManager: SessionManager
) : NotificacionRepository {

    private val collection = firestore.collection("notificaciones")
    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
    override val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

    private val startTime = System.currentTimeMillis()
    private val shownNotifIds = mutableSetOf<String>()

    init {
        // Escuchamos notificaciones en tiempo real
        collection.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val currentUserId = sessionManager.getSession()
                    val list = snapshot.toObjects(Notificacion::class.java)
                    
                    // REGLA: Solo disparar banner si la notificación es NUEVA (creada después de abrir la app)
                    list.filter { it.timestamp > startTime && it.userId == currentUserId && !it.leida }
                        .forEach { newNotif ->
                            if (!shownNotifIds.contains(newNotif.id)) {
                                showLocalNotification(newNotif.titulo, newNotif.mensaje)
                                shownNotifIds.add(newNotif.id)
                            }
                        }
                    
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
        val argsArray = mensajeArgs.toTypedArray()
        
        // CORRECCIÓN: Formatear AMBOS (título y mensaje) para que no salga %1$s
        val titulo = try {
            resourceProvider.getString(tituloResId, *argsArray)
        } catch (e: Exception) {
            resourceProvider.getString(tituloResId)
        }
        
        val mensaje = try {
            resourceProvider.getString(mensajeResId, *argsArray)
        } catch (e: Exception) {
            resourceProvider.getString(mensajeResId)
        }
        
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

    private fun showLocalNotification(title: String, message: String) {
        val context = resourceProvider.context
        val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val channelId = "petadopt_realtime"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(channelId, "Alertas PetAdopt", android.app.NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(com.example.seguimiento.R.drawable.petadopticono)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
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
