package com.example.seguimiento.features.Estadisticas

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class BarData(val label: String, val valueBlue: Int, val valueOrange: Int = 0)

data class EstadisticasUiState(
    val solicitudesNuevas: Int = 25,
    val mascotasTotales: Int = 150,
    val mascotasActivas: Int = 150,
    val usuariosTotales: Int = 500,
    val pantallaSeleccionada: Int = 3,
    val estadisticasSemanales: List<BarData> = listOf(
        BarData("Mon", 150), BarData("May", 260),
        BarData("Jun", 100, 120), BarData("Jun", 0, 170),
        BarData("Ven", 220, 230), BarData("San", 0, 330)
    )
)

@HiltViewModel
class EstadisticasViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(EstadisticasUiState())
    val uiState: StateFlow<EstadisticasUiState> = _uiState.asStateFlow()

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(pantallaSeleccionada = index) }
    }
}
