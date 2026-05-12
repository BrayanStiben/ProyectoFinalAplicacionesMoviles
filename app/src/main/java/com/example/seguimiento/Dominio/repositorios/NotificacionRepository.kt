package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.Notificacion
import kotlinx.coroutines.flow.StateFlow

interface NotificacionRepository {
    val notificaciones: StateFlow<List<Notificacion>>
    
    suspend fun addNotificacion(
        tituloResId: Int,
        mensajeResId: Int,
        mensajeArgs: List<Any> = emptyList(),
        tipo: String = "INFO",
        userId: String? = null
    )
    
    suspend fun deleteNotificacion(id: String)
    suspend fun clearAll(userId: String)
    suspend fun marcarComoLeida(id: String)
}
