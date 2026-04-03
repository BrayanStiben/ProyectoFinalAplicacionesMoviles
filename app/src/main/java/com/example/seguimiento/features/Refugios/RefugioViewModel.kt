package com.example.seguimiento.features.Refugios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Refugio
import com.example.seguimiento.Dominio.modelos.RefugioEstado
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.Dominio.repositorios.RefugioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RefugioViewModel @Inject constructor(
    private val refugioRepository: RefugioRepository,
    private val authRepository: AuthRepository,
    private val notificacionRepository: NotificacionRepository
) : ViewModel() {

    val refugiosAprobados: StateFlow<List<Refugio>> = refugioRepository.refugios
        .map { list -> list.filter { it.estado == RefugioEstado.APROBADO } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val solicitudesPendientes: StateFlow<List<Refugio>> = refugioRepository.refugios
        .map { list -> list.filter { it.estado == RefugioEstado.PENDIENTE } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun registrarRefugio(
        nombre: String,
        direccion: String,
        telefono: String,
        descripcion: String,
        fotoUrl: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val userId = authRepository.currentUser.value?.id ?: "anonimo"
            val nuevo = Refugio(
                id = UUID.randomUUID().toString(),
                nombre = nombre,
                direccion = direccion,
                telefono = telefono,
                descripcion = descripcion,
                imagenUrl = fotoUrl,
                estado = RefugioEstado.PENDIENTE,
                autorId = userId
            )
            refugioRepository.save(nuevo)
            
            notificacionRepository.addNotificacion(
                titulo = "Refugio en revisión 🏠",
                mensaje = "Tu solicitud para registrar el refugio '$nombre' ha sido enviada.",
                userId = userId
            )
            onSuccess()
        }
    }

    fun aprobarRefugio(id: String) {
        viewModelScope.launch {
            val refugio = refugioRepository.getById(id)
            refugioRepository.actualizarEstado(id, RefugioEstado.APROBADO)
            
            refugio?.let {
                notificacionRepository.addNotificacion(
                    titulo = "¡Refugio Aprobado! 🎉",
                    mensaje = "Tu refugio '${it.nombre}' ya es visible para toda la comunidad.",
                    userId = it.autorId
                )
            }
        }
    }

    fun rechazarRefugio(id: String) {
        viewModelScope.launch {
            val refugio = refugioRepository.getById(id)
            refugioRepository.actualizarEstado(id, RefugioEstado.RECHAZADO)
            
            refugio?.let {
                notificacionRepository.addNotificacion(
                    titulo = "Solicitud de Refugio Rechazada ❌",
                    mensaje = "Lo sentimos, el registro de '${it.nombre}' no ha sido aprobado.",
                    userId = it.autorId
                )
            }
        }
    }
}
