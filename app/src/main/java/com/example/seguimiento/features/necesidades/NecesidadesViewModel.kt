package com.example.seguimiento.features.necesidades

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NecesidadesViewModel @Inject constructor() : ViewModel() {

    private val _peso = MutableStateFlow("")
    val peso: StateFlow<String> = _peso.asStateFlow()

    private val _actividad = MutableStateFlow("Moderada")
    val actividad: StateFlow<String> = _actividad.asStateFlow()

    private val _resultadoCalorias = MutableStateFlow(0)
    val resultadoCalorias: StateFlow<Int> = _resultadoCalorias.asStateFlow()

    fun onPesoChanged(nuevoPeso: String) {
        if (nuevoPeso.isEmpty() || nuevoPeso.toDoubleOrNull() != null) {
            _peso.value = nuevoPeso
            calcularCalorias()
        }
    }

    fun onActividadChanged(nuevaActividad: String) {
        _actividad.value = nuevaActividad
        calcularCalorias()
    }

    private fun calcularCalorias() {
        val p = _peso.value.toDoubleOrNull() ?: return
        val factor = when (_actividad.value) {
            "Baja" -> 70.0
            "Moderada" -> 90.0
            "Alta" -> 110.0
            else -> 90.0
        }
        // Fórmula básica: RER = 70 * (peso)^0.75 modificado por factor actividad
        val rer = factor * Math.pow(p, 0.75)
        _resultadoCalorias.value = rer.toInt()
    }
}
