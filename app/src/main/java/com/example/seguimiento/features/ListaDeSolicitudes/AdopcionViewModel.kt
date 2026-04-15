package com.example.seguimiento.features.ListaDeSolicitudes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.LogrosRepository
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
    private val notificacionRepository: NotificacionRepository,
    private val logrosRepository: LogrosRepository
) : ViewModel() {

    val solicitudes: StateFlow<List<Mascota>> = mascotaRepository.mascotas
        .map { list -> list.filter { it.estado == PublicacionEstado.PENDIENTE } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun aprobarPublicacion(id: String) {
        viewModelScope.launch {
            val mascota = mascotaRepository.getById(id)
            mascotaRepository.actualizarEstado(id, PublicacionEstado.VERIFICADA)
            
            if (mascota != null) {
                // LOGROS DE RESCATE
                logrosRepository.ganarLogro(mascota.autorId, "res_1")
                
                val misPublicacionesAprobadas = mascotaRepository.mascotas.value.count { 
                    it.autorId == mascota.autorId && it.estado == PublicacionEstado.VERIFICADA 
                }
                if (misPublicacionesAprobadas >= 3) {
                    logrosRepository.ganarLogro(mascota.autorId, "res_3")
                }

                notificacionRepository.addNotificacion(
                    tituloResId = R.string.notif_pet_approved_title,
                    mensajeResId = R.string.notif_pet_approved_msg,
                    mensajeArgs = listOf(mascota.nombre),
                    tipo = "INFO",
                    userId = mascota.autorId
                )
            }
        }
    }

    fun rechazarPublicacion(id: String, motivo: String) {
        viewModelScope.launch {
            val mascota = mascotaRepository.getById(id)
            mascotaRepository.actualizarEstado(id, PublicacionEstado.RECHAZADA, motivo)
            
            if (mascota != null) {
                // GANAR LOGRO: Popular (por rechazo de publicación)
                logrosRepository.ganarLogro(mascota.autorId, "per_2")

                notificacionRepository.addNotificacion(
                    tituloResId = R.string.notif_pet_rejected_title,
                    mensajeResId = R.string.notif_pet_rejected_msg,
                    mensajeArgs = listOf(mascota.nombre, motivo),
                    tipo = "INFO",
                    userId = mascota.autorId
                )
            }
        }
    }
}
