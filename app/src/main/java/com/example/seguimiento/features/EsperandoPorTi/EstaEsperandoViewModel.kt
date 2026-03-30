package com.example.seguimiento.features.EsperandoPorTi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Comentario
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.repositorios.ComentarioRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EstaEsperandoViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository,
    private val comentarioRepository: ComentarioRepository
) : ViewModel() {

    private val _mascotaId = MutableStateFlow<String?>(null)
    
    val mascota = _mascotaId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else flowOf(mascotaRepository.getById(id))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val comentarios = _mascotaId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else comentarioRepository.getComentariosPorMascota(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun seleccionarMascota(id: String) {
        _mascotaId.value = id
    }

    fun agregarComentario(texto: String, autorId: String, autorNombre: String) {
        val currentId = _mascotaId.value ?: return
        viewModelScope.launch {
            val nuevoComentario = Comentario(
                id = UUID.randomUUID().toString(),
                mascotaId = currentId,
                autorId = autorId,
                autorNombre = autorNombre,
                contenido = texto
            )
            comentarioRepository.agregarComentario(nuevoComentario)
            // Aquí se dispararía la notificación al autor de la mascota (FCM)
        }
    }
}
