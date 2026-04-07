package com.example.seguimiento.features.EsperandoPorTi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.AdoptionRequest
import com.example.seguimiento.Dominio.modelos.AdoptionRequestStatus
import com.example.seguimiento.Dominio.modelos.Comentario
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.AdoptionRepository
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.ComentarioRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EstaEsperandoViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository,
    private val comentarioRepository: ComentarioRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val adoptionRepository: AdoptionRepository,
    private val notificacionRepository: NotificacionRepository
) : ViewModel() {

    private val _mascotaId = MutableStateFlow<String?>(null)
    
    val currentUser = authRepository.currentUser.flatMapLatest { user ->
        if (user == null) flowOf(null)
        else userRepository.users.map { list -> list.find { it.id == user.id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val mascota = _mascotaId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else flowOf(mascotaRepository.getById(id))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val comentarios: StateFlow<List<Comentario>> = _mascotaId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else comentarioRepository.getComentariosPorTarget(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentRequest = combine(_mascotaId, adoptionRepository.requests, currentUser) { id, requests, user ->
        if (id == null || user == null) null
        else requests.find { it.mascotaId == id && it.userId == user.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val approvedRequest = currentRequest.map { 
        if (it?.status == AdoptionRequestStatus.APPROVED) it else null 
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val rejectedRequest = currentRequest.map { 
        if (it?.status == AdoptionRequestStatus.REJECTED) it else null 
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isPenalized: StateFlow<Boolean> = combine(currentUser, flow {
        while(true) {
            emit(System.currentTimeMillis())
            kotlinx.coroutines.delay(1000)
        }
    }) { user, now ->
        user != null && now < user.penaltyEndTime
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val penaltyTimeLeft: StateFlow<String> = combine(currentUser, flow {
        while(true) {
            emit(System.currentTimeMillis())
            kotlinx.coroutines.delay(1000)
        }
    }) { user, now ->
        if (user == null || now >= user.penaltyEndTime) ""
        else {
            val diff = user.penaltyEndTime - now
            val seconds = (diff / 1000) % 60
            val minutes = (diff / (1000 * 60)) % 60
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun seleccionarMascota(id: String) {
        _mascotaId.value = id
    }

    fun toggleLike() {
        val currentMascotaId = _mascotaId.value ?: return
        val user = currentUser.value ?: return
        val m = mascota.value ?: return
        
        mascotaRepository.toggleLike(currentMascotaId, user.id)
        
        // NOTIFICACIÓN: Like
        if (m.autorId != user.id) {
            notificacionRepository.addNotificacion(
                titulo = "¡Nuevo Like! ❤️",
                mensaje = "A ${user.name} le gusta tu publicación de ${m.nombre}.",
                tipo = "POST_VOTADO",
                userId = m.autorId
            )
        }
    }

    fun cancelarSolicitud(requestId: String) {
        viewModelScope.launch {
            val request = adoptionRepository.getById(requestId)
            adoptionRepository.deleteRequest(requestId)
            
            // NOTIFICACIÓN: Cancelación
            if (request != null) {
                notificacionRepository.addNotificacion(
                    titulo = "Solicitud cancelada ⚠️",
                    mensaje = "Has cancelado tu solicitud de adopción para ${request.petName}.",
                    tipo = "INFO",
                    userId = request.userId
                )
            }
        }
    }

    fun agregarComentario(texto: String, parentId: String? = null) {
        val currentId = _mascotaId.value ?: return
        val user = currentUser.value
        val m = mascota.value ?: return
        viewModelScope.launch {
            val nuevoComentario = Comentario(
                id = UUID.randomUUID().toString(),
                targetId = currentId,
                autorId = user?.id ?: "anonimo",
                autorNombre = user?.name ?: "Usuario",
                contenido = texto,
                parentId = parentId
            )
            comentarioRepository.agregarComentario(nuevoComentario)

            // NOTIFICACIÓN: Comentario nuevo al dueño de la mascota
            if (user != null && m.autorId != user.id) {
                notificacionRepository.addNotificacion(
                    titulo = "Nuevo comentario 💬",
                    mensaje = "${user.name} comentó en la publicación de ${m.nombre}.",
                    tipo = "COMENTARIO_NUEVO",
                    userId = m.autorId
                )
            }
        }
    }
}
