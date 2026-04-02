package com.example.seguimiento.features.HistoriaMascota

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.HistoriaEstado
import com.example.seguimiento.Dominio.modelos.HistoriaFeliz
import com.example.seguimiento.Dominio.modelos.UserRole
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.HistoriaFelizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoriaMascotaViewModel @Inject constructor(
    private val repository: HistoriaFelizRepository,
    private val authRepository: AuthRepository
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

    val historiasPendientes = repository.historias.map { lista ->
        lista.filter { it.estado == HistoriaEstado.PENDIENTE }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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
        // Reset fields
        _textoHistoria.value = ""
        _mascotaNombre.value = ""
        _imagenSeleccionada.value = null
        onSuccess()
    }

    fun aprobarHistoria(id: String) {
        repository.actualizarEstado(id, HistoriaEstado.APROBADA)
    }

    fun rechazarHistoria(id: String) {
        repository.actualizarEstado(id, HistoriaEstado.RECHAZADA)
    }
}
