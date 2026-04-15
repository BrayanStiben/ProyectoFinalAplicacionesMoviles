package com.example.seguimiento.features.GestionComentarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Comentario
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.ComentarioRepository
import com.example.seguimiento.Dominio.repositorios.LogrosRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class MascotaConComentarios(
    val mascota: Mascota,
    val comentarios: List<Comentario>
)

@HiltViewModel
class GestionComentariosViewModel @Inject constructor(
    private val comentarioRepository: ComentarioRepository,
    private val mascotaRepository: MascotaRepository,
    private val authRepository: AuthRepository,
    private val logrosRepository: LogrosRepository,
    private val notificacionRepository: NotificacionRepository
) : ViewModel() {

    val mascotasConComentarios: StateFlow<List<MascotaConComentarios>> = combine(
        mascotaRepository.mascotas,
        comentarioRepository.todosLosComentarios
    ) { mascotas, comentarios ->
        mascotas.mapNotNull { mascota ->
            val comentariosMascota = comentarios.filter { it.targetId == mascota.id }
            if (comentariosMascota.isNotEmpty()) {
                MascotaConComentarios(mascota, comentariosMascota)
            } else null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun eliminarComentario(id: String) {
        viewModelScope.launch {
            val comentario = comentarioRepository.todosLosComentarios.value.find { it.id == id }
            comentarioRepository.eliminarComentario(id)
            
            if (comentario != null) {
                // LOGRO NEGATIVO: Comentario Imprudente
                logrosRepository.ganarLogro(comentario.autorId, "sys_imprudente")
                
                // NOTIFICACIÓN: Comentario eliminado - Usando recursos para traducción
                notificacionRepository.addNotificacion(
                    tituloResId = R.string.notif_comment_deleted_title,
                    mensajeResId = R.string.notif_comment_deleted_msg,
                    tipo = "INFO",
                    userId = comentario.autorId
                )
            }
        }
    }

    fun censurarComentario(id: String) {
        viewModelScope.launch {
            val comentario = comentarioRepository.todosLosComentarios.value.find { it.id == id }
            comentarioRepository.censurarComentario(id, "[Este comentario ha sido censurado por el administrador]")
            
            if (comentario != null) {
                // LOGRO NEGATIVO: Comentario Imprudente
                logrosRepository.ganarLogro(comentario.autorId, "sys_imprudente")
            }
        }
    }

    fun responderComentario(targetId: String, texto: String, parentId: String?) {
        val user = authRepository.currentUser.value
        viewModelScope.launch {
            val respuesta = Comentario(
                id = UUID.randomUUID().toString(),
                targetId = targetId,
                autorId = user?.id ?: "admin",
                autorNombre = user?.name ?: "Administrador",
                contenido = texto,
                parentId = parentId
            )
            comentarioRepository.agregarComentario(comentario = respuesta)
        }
    }
}
