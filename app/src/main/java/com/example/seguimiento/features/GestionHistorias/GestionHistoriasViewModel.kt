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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestionHistoriasViewModel @Inject constructor(
    private val repository: HistoriaFelizRepository,
    private val notificacionRepository: com.example.seguimiento.Dominio.repositorios.NotificacionRepository
) : ViewModel() {

    val historiasPendientes: StateFlow<List<HistoriaFeliz>> = repository.historias.map { lista ->
        lista.filter { it.estado == HistoriaEstado.PENDIENTE }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun aprobarHistoria(id: String) {
        viewModelScope.launch {
            val historia = repository.getById(id)
            repository.actualizarEstado(id, HistoriaEstado.APROBADA)
            
            historia?.let {
                notificacionRepository.addNotificacion(
                    tituloResId = com.example.seguimiento.R.string.stories_notif_approved_title,
                    mensajeResId = com.example.seguimiento.R.string.stories_notif_approved_msg,
                    mensajeArgs = listOf(it.mascotaNombre),
                    tipo = "INFO",
                    userId = it.autorId
                )
            }
        }
    }

    fun rechazarHistoria(id: String) {
        viewModelScope.launch {
            val historia = repository.getById(id)
            repository.actualizarEstado(id, HistoriaEstado.RECHAZADA)
            
            historia?.let {
                notificacionRepository.addNotificacion(
                    tituloResId = com.example.seguimiento.R.string.stories_notif_rejected_title,
                    mensajeResId = com.example.seguimiento.R.string.stories_notif_rejected_msg,
                    mensajeArgs = listOf(it.mascotaNombre),
                    tipo = "INFO",
                    userId = it.autorId
                )
            }
        }
    }
}
