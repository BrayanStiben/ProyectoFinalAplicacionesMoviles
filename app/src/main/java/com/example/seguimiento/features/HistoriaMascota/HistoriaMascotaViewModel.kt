package com.example.seguimiento.features.HistoriaMascota

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.*
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.HistoriaFelizRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.Dominio.repositorios.ComentarioRepository
import com.example.seguimiento.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HistoriaMascotaViewModel @Inject constructor(
    private val repository: HistoriaFelizRepository,
    val authRepository: AuthRepository,
    private val notificacionRepository: NotificacionRepository,
    private val comentarioRepository: ComentarioRepository
) : ViewModel() {

    private val _textoHistoria = MutableStateFlow("")
    val textoHistoria: StateFlow<String> = _textoHistoria.asStateFlow()

    private val _mascotaNombre = MutableStateFlow("")
    val mascotaNombre: StateFlow<String> = _mascotaNombre.asStateFlow()

    private val _imagenSeleccionada = MutableStateFlow<Uri?>(null)
    val imagenSeleccionada: StateFlow<Uri?> = _imagenSeleccionada.asStateFlow()

    val historiasAprobadas = repository.historias.map { lista ->
        lista.filter { it.estado == HistoriaEstado.APROBADA }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val todasLasHistorias = repository.historias
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun alCambiarTexto(nuevoTexto: String) {
        _textoHistoria.value = nuevoTexto
    }

    fun alCambiarNombreMascota(nuevoNombre: String) {
        _mascotaNombre.value = nuevoNombre
    }

    fun alSeleccionarImagen(uri: Uri?) {
        _imagenSeleccionada.value = uri
    }

    fun compartir(onSuccess: () -> Unit = {}) {
        val currentUser = authRepository.currentUser.value
        val esAdmin = currentUser?.role == UserRole.ADMIN
        
        val historia = HistoriaFeliz(
            autorId = currentUser?.id ?: "anonimo",
            autorNombre = currentUser?.name ?: "Usuario",
            mascotaNombre = _mascotaNombre.value.ifBlank { "Mascota" },
            texto = _textoHistoria.value,
            imagenUrl = _imagenSeleccionada.value?.toString() ?: "https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=500",
            estado = if (esAdmin) HistoriaEstado.APROBADA else HistoriaEstado.PENDIENTE
        )
        repository.save(historia)

        if (currentUser != null) {
            notificacionRepository.addNotificacion(
                tituloResId = R.string.stories_notif_shared_title,
                mensajeResId = R.string.stories_notif_shared_msg,
                mensajeArgs = listOf(_mascotaNombre.value),
                userId = currentUser.id
            )
        }

        _textoHistoria.value = ""
        _mascotaNombre.value = ""
        _imagenSeleccionada.value = null
        onSuccess()
    }

    fun editarHistoria(id: String, nuevoTexto: String, nuevoNombre: String) {
        val historia = repository.historias.value.find { it.id == id } ?: return
        repository.save(historia.copy(texto = nuevoTexto, mascotaNombre = nuevoNombre))
    }

    fun toggleLike(historiaId: String) {
        val userId = authRepository.currentUser.value?.id ?: return
        repository.toggleLike(historiaId, userId)
    }

    fun toggleFollow(historiaId: String) {
        val userId = authRepository.currentUser.value?.id ?: return
        repository.toggleFollow(historiaId, userId)
    }

    fun getComentarios(historiaId: String) = comentarioRepository.getComentariosPorTarget(historiaId)

    fun agregarComentario(historiaId: String, contenido: String, parentId: String? = null) {
        val user = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            val nuevo = Comentario(
                id = UUID.randomUUID().toString(),
                targetId = historiaId,
                autorId = user.id,
                autorNombre = user.name,
                contenido = contenido,
                parentId = parentId
            )
            comentarioRepository.agregarComentario(nuevo)
        }
    }

    fun eliminarHistoria(id: String) {
        repository.delete(id)
    }

    fun aprobarHistoria(id: String) {
        viewModelScope.launch {
            val historia = repository.historias.value.find { it.id == id }
            repository.actualizarEstado(id, HistoriaEstado.APROBADA)
            
            if (historia != null) {
                notificacionRepository.addNotificacion(
                    tituloResId = R.string.stories_notif_approved_title,
                    mensajeResId = R.string.stories_notif_approved_msg,
                    mensajeArgs = listOf(historia.mascotaNombre),
                    userId = historia.autorId
                )
            }
        }
    }

    fun rechazarHistoria(id: String) {
        viewModelScope.launch {
            val historia = repository.historias.value.find { it.id == id }
            repository.actualizarEstado(id, HistoriaEstado.RECHAZADA)

            if (historia != null) {
                notificacionRepository.addNotificacion(
                    tituloResId = R.string.stories_notif_rejected_title,
                    mensajeResId = R.string.stories_notif_rejected_msg,
                    mensajeArgs = listOf(historia.mascotaNombre),
                    userId = historia.autorId
                )
            }
        }
    }
}
