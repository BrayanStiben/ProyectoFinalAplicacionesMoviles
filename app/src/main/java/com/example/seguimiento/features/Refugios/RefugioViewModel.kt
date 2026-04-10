package com.example.seguimiento.features.Refugios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Refugio
import com.example.seguimiento.Dominio.modelos.RefugioEstado
import com.example.seguimiento.Dominio.modelos.RefugioTipo
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.Dominio.repositorios.RefugioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import com.example.seguimiento.R

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
        registrarRefugioConTipo(nombre, direccion, telefono, descripcion, fotoUrl, RefugioTipo.REFUGIO, onSuccess)
    }

    fun registrarRefugioConTipo(
        nombre: String,
        direccion: String,
        telefono: String,
        descripcion: String,
        fotoUrl: String,
        tipo: RefugioTipo,
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
                autorId = userId,
                tipo = tipo
            )
            refugioRepository.save(nuevo)
            
            val etiqueta = if (tipo == RefugioTipo.VETERINARIA) "veterinaria" else "refugio"
            notificacionRepository.addNotificacion(
                tituloResId = R.string.notif_refugio_review_title,
                mensajeResId = R.string.notif_refugio_review_msg,
                mensajeArgs = listOf(etiqueta, nombre),
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
                val etiqueta = if (it.tipo == RefugioTipo.VETERINARIA) "Veterinaria" else "Refugio"
                notificacionRepository.addNotificacion(
                    tituloResId = R.string.notif_refugio_approved_title,
                    mensajeResId = R.string.notif_refugio_approved_msg,
                    mensajeArgs = listOf(etiqueta, it.nombre),
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
                val etiqueta = if (it.tipo == RefugioTipo.VETERINARIA) "veterinaria" else "refugio"
                notificacionRepository.addNotificacion(
                    tituloResId = R.string.notif_refugio_rejected_title,
                    mensajeResId = R.string.notif_refugio_rejected_msg,
                    mensajeArgs = listOf(etiqueta, it.nombre),
                    userId = it.autorId
                )
            }
        }
    }
}
