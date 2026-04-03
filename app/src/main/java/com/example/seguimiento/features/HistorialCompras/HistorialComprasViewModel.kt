package com.example.seguimiento.features.HistorialCompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.CompraTienda
import com.example.seguimiento.Dominio.repositorios.TiendaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistorialComprasViewModel @Inject constructor(
    private val tiendaRepository: TiendaRepository
) : ViewModel() {

    val historialCompras: StateFlow<List<CompraTienda>> = tiendaRepository.compras
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
