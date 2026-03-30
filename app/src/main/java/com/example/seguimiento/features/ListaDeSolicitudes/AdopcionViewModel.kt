package com.example.seguimiento.features.ListaDeSolicitudes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdopcionViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository
) : ViewModel() {

    val solicitudes: StateFlow<List<Mascota>> = mascotaRepository.mascotas
        .map { list -> list.filter { it.estado == PublicacionEstado.PENDIENTE } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun aprobarPublicacion(id: String) {
        viewModelScope.launch {
            mascotaRepository.actualizarEstado(id, PublicacionEstado.VERIFICADA)
        }
    }

    fun rechazarPublicacion(id: String, motivo: String) {
        viewModelScope.launch {
            mascotaRepository.actualizarEstado(id, PublicacionEstado.RECHAZADA, motivo)
        }
    }
}
