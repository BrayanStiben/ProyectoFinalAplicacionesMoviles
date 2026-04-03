package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.Notificacion
import kotlinx.coroutines.flow.StateFlow

interface NotificacionRepository {
    val notificaciones: StateFlow<List<Notificacion>>
    fun addNotificacion(titulo: String, mensaje: String, tipo: String = "INFO", userId: String = "")
    fun marcarComoLeida(id: String)
    fun clearAll()
}
