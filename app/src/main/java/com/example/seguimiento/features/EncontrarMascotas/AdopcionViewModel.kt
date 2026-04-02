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

    // Exponemos todas las mascotas (incluye quemadas de API y creadas por usuarios)
    // El admin ve todo lo que está verificado o ya resuelto/adoptado para gestión
    val listaMascotas: StateFlow<List<Mascota>> = mascotaRepository.mascotas
        .map { lista -> 
            lista.filter { 
                it.estado == PublicacionEstado.VERIFICADA || 
                it.estado == PublicacionEstado.RESUELTA ||
                it.estado == PublicacionEstado.ADOPTADA
            } 
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun eliminarMascota(id: String) {
        viewModelScope.launch {
            mascotaRepository.delete(id)
        }
    }

    fun adoptar(mascotaId: String) {
        viewModelScope.launch {
            // Lógica de adopción si fuera necesaria aquí
        }
    }
}
