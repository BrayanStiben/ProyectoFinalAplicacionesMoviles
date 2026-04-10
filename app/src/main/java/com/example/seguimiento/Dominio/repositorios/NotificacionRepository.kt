package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.Notificacion
import kotlinx.coroutines.flow.StateFlow

interface NotificacionRepository {
    val notificaciones: StateFlow<List<Notificacion>>
    fun addNotificacion(
        titulo: String = "",
        tituloResId: Int? = null,
        tituloArgs: List<String> = emptyList(),
        mensaje: String = "",
        mensajeResId: Int? = null,
        mensajeArgs: List<String> = emptyList(),
        tipo: String = "INFO",
        userId: String = ""
    )
    fun marcarComoLeida(id: String)
    fun clearAll()
}
