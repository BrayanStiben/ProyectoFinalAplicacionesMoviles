package com.example.seguimiento.features.EncontrarMascotas

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

    // El admin ve todas las mascotas para gestión completa
    val listaMascotas: StateFlow<List<Mascota>> = mascotaRepository.mascotas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun eliminarMascota(id: String) {
        viewModelScope.launch {
            mascotaRepository.delete(id)
        }
    }
}
