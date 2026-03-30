package com.example.seguimiento.features.Notificaciones

import androidx.lifecycle.ViewModel
import com.example.seguimiento.Dominio.modelos.Notificacion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NotificacionesViewModel @Inject constructor() : ViewModel() {

    private val _notificaciones = MutableStateFlow<List<Notificacion>>(listOf(
        Notificacion("1", "¡Bienvenido!", "Gracias por unirte a PetAdopta."),
        Notificacion("2", "Nueva mascota cerca", "Un nuevo perrito ha sido publicado en tu zona.", tipo = "ZONA_NUEVA"),
        Notificacion("3", "Publicación Verificada", "Tu publicación de 'Rex' ha sido aprobada por un moderador.", tipo = "INFO"),
        Notificacion("4", "Nuevo Comentario", "Alguien ha comentado en tu publicación.", tipo = "COMENTARIO_NUEVO")
    ))
    val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

    fun marcarComoLeida(id: String) {
        _notificaciones.value = _notificaciones.value.map {
            if (it.id == id) it.copy(leida = true) else it
        }
    }
}
