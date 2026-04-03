package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Notificacion
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificacionRepositoryImpl @Inject constructor() : NotificacionRepository {

    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
    override val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

    override fun addNotificacion(titulo: String, mensaje: String, tipo: String, userId: String) {
        val nueva = Notificacion(
            id = UUID.randomUUID().toString(),
            titulo = titulo,
            mensaje = mensaje,
            userId = userId, // Ahora sí se guarda el userId
            tipo = tipo
        )
        _notificaciones.update { listOf(nueva) + it }
    }

    override fun marcarComoLeida(id: String) {
        _notificaciones.update { lista ->
            lista.map { if (it.id == id) it.copy(leida = true) else it }
        }
    }

    override fun clearAll() {
        _notificaciones.value = emptyList()
    }
}
