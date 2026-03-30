package com.example.seguimiento.features.Filtros

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FiltroViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository
) : ViewModel() {
    
    // Estado de los filtros
    var tipoMascotaSeleccionado by mutableStateOf("Perro")
    var razaSeleccionada by mutableStateOf("")
    var radioGps by mutableStateOf(10f)
    var estaVacunado by mutableStateOf(true)
    var estaEsterilizado by mutableStateOf(true)

    // El feed de resultados filtrados
    val resultadosFiltrados: StateFlow<List<Mascota>> = mascotaRepository.mascotas
        .map { lista ->
            lista.filter { mascota ->
                val esVisible = mascota.estado == PublicacionEstado.VERIFICADA || 
                              mascota.estado == PublicacionEstado.RESUELTA
                
                val coincideTipo = mascota.tipo.equals(tipoMascotaSeleccionado, ignoreCase = true)
                val coincideRaza = razaSeleccionada.isEmpty() || mascota.raza.contains(razaSeleccionada, ignoreCase = true)
                
                // En una app real aquí aplicaríamos la lógica de radioGps con lat/lng
                
                esVisible && coincideTipo && coincideRaza
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun limpiarFiltros() {
        tipoMascotaSeleccionado = "Perro"
        razaSeleccionada = ""
        radioGps = 10f
        estaVacunado = false
        estaEsterilizado = false
    }
}
