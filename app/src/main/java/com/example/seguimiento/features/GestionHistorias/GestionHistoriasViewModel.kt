package com.example.seguimiento.features.GestionHistorias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.HistoriaEstado
import com.example.seguimiento.Dominio.modelos.HistoriaFeliz
import com.example.seguimiento.Dominio.repositorios.HistoriaFelizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GestionHistoriasViewModel @Inject constructor(
    private val repository: HistoriaFelizRepository
) : ViewModel() {

    val historiasPendientes: StateFlow<List<HistoriaFeliz>> = repository.historias.map { lista ->
        lista.filter { it.estado == HistoriaEstado.PENDIENTE }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun aprobarHistoria(id: String) {
        repository.actualizarEstado(id, HistoriaEstado.APROBADA)
    }

    fun rechazarHistoria(id: String) {
        repository.actualizarEstado(id, HistoriaEstado.RECHAZADA)
    }
}
