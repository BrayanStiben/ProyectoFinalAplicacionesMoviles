package com.example.seguimiento.features.EncontrarMascotas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdopcionViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository,
    private val notificacionRepository: NotificacionRepository
) : ViewModel() {

    // El admin ve todas las mascotas para gestión completa
    val listaMascotas: StateFlow<List<Mascota>> = mascotaRepository.mascotas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun eliminarMascota(id: String) {
        viewModelScope.launch {
            val mascota = mascotaRepository.getById(id)
            mascotaRepository.delete(id)
            
            if (mascota != null) {
                // NOTIFICACIÓN: Publicación Eliminada - Usando recursos
                notificacionRepository.addNotificacion(
                    tituloResId = R.string.pet_mgmt_delete_title,
                    mensajeResId = R.string.pet_mgmt_delete_confirm,
                    mensajeArgs = listOf(mascota.nombre),
                    tipo = "INFO",
                    userId = mascota.autorId
                )
            }
        }
    }

    fun aprobarMascota(id: String) {
        viewModelScope.launch {
            mascotaRepository.actualizarEstado(id, PublicacionEstado.VERIFICADA)
            
            val mascota = mascotaRepository.getById(id)
            if (mascota != null) {
                notificacionRepository.addNotificacion(
                    tituloResId = R.string.ai_approval_title,
                    mensajeResId = R.string.ai_approval_msg,
                    mensajeArgs = listOf(mascota.nombre),
                    tipo = "SUCCESS",
                    userId = mascota.autorId
                )
            }
        }
    }

    fun rechazarMascota(id: String, motivo: String) {
        viewModelScope.launch {
            mascotaRepository.actualizarEstado(id, PublicacionEstado.RECHAZADA, motivo)
            
            val mascota = mascotaRepository.getById(id)
            if (mascota != null) {
                notificacionRepository.addNotificacion(
                    tituloResId = R.string.ai_rejection_title,
                    mensajeResId = R.string.ai_rejection_msg,
                    mensajeArgs = listOf(mascota.nombre, motivo),
                    tipo = "ERROR",
                    userId = mascota.autorId
                )
            }
        }
    }
}
