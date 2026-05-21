package com.example.seguimiento.Data.repositorios

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.seguimiento.Dominio.modelos.Notificacion
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.MainActivity
import com.example.seguimiento.core.utils.ResourceProvider
import com.example.seguimiento.core.utils.SessionManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificacionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val resourceProvider: ResourceProvider,
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
) : NotificacionRepository {

    private val collection = firestore.collection("notificaciones")
    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
    override val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

    private val shownBannersInSession = mutableSetOf<String>()
    private var lastLoggedUserId: String? = null

    init {
        // ESCUCHA TOTALMENTE PROACTIVA
        collection.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (snapshot == null || error != null) return@addSnapshotListener

                val list = snapshot.toObjects(Notificacion::class.java)
                _notificaciones.value = list

                processNotifications(list)
            }

        // NUEVO: Observar cambios de usuario para re-disparar notificaciones pendientes al iniciar sesión
        // y limpiar la caché de banners al cerrar sesión
        CoroutineScope(Dispatchers.Main).launch {
            authRepository.currentUser.collect { user ->
                if (user == null) {
                    shownBannersInSession.clear()
                    lastLoggedUserId = null
                } else {
                    // Al iniciar sesión o cambiar de usuario, procesar lo que ya tengamos
                    processNotifications(_notificaciones.value)
                }
            }
        }
    }

    private fun processNotifications(list: List<Notificacion>) {
        val currentUserId = sessionManager.getSession() ?: return

        // Al cambiar de cuenta, permitimos que vuelvan a saltar banners
        if (currentUserId != lastLoggedUserId) {
            shownBannersInSession.clear()
            lastLoggedUserId = currentUserId
        }

        // FLUJO: Si hay notificaciones NO LEÍDAS, disparamos banner PUSH de inmediato
        list.filter { it.userId == currentUserId && !it.leida }
            .forEach { notif ->
                if (!shownBannersInSession.contains(notif.id)) {
                    triggerSystemPushBanner(notif.titulo, notif.mensaje)
                    shownBannersInSession.add(notif.id)
                }
            }
    }

    override suspend fun addNotificacion(tituloResId: Int, mensajeResId: Int, mensajeArgs: List<Any>, tipo: String, userId: String?) {
        val args = mensajeArgs.toTypedArray()
        val tFinal = try { resourceProvider.getString(tituloResId, *args) } catch (e: Exception) { resourceProvider.getString(tituloResId) }
        val mFinal = try { resourceProvider.getString(mensajeResId, *args) } catch (e: Exception) { resourceProvider.getString(mensajeResId) }
        
        val id = collection.document().id
        val notif = Notificacion(
            id = id,
            titulo = tFinal,
            mensaje = mFinal,
            tipo = tipo,
            timestamp = System.currentTimeMillis(),
            userId = userId ?: "",
            leida = false
        )
        collection.document(id).set(notif)
    }

    private fun triggerSystemPushBanner(title: String, message: String) {
        val context = resourceProvider.context
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "petadopt_notif_channel_v1" // ID único y descriptivo

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alertas PetAdopt", NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(true)
                description = "Notificaciones de estado en tiempo real"
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setShowBadge(true)
            }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 
            System.currentTimeMillis().toInt(), // ID único para el PendingIntent
            intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(com.example.seguimiento.R.drawable.petadopticono)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), builder)
    }

    override suspend fun deleteNotificacion(id: String) {
        collection.document(id).delete()
    }

    override suspend fun clearAll(userId: String) {
        collection.whereEqualTo("userId", userId).get().addOnSuccessListener { snapshot ->
            val batch = firestore.batch()
            snapshot.documents.forEach { batch.delete(it.reference) }
            batch.commit()
        }
    }

    override suspend fun marcarComoLeida(id: String) {
        collection.document(id).update("leida", true)
    }
}
