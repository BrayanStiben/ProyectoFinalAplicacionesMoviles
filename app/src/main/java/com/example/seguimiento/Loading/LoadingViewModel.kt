package com.example.seguimiento.Loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoadingViewModel : ViewModel() {

    // Estado del progreso (0.0f a 1.0f)
    private val _progress = MutableStateFlow(0.0f)
    val progress = _progress.asStateFlow()

    // Estado para indicar cuándo terminar la carga
    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()

    init {
        startLoading()
    }

    private fun startLoading() {
        viewModelScope.launch {
            // Simulamos una carga progresiva de unos 3.5 segundos
            // Esto se ajustará a la velocidad real de carga de tu app
            for (i in 0..100) {
                delay(35) // Pequeño delay para suavizar
                _progress.value = i / 100.0f
            }
            // Marcamos como finalizado
            _isFinished.value = true
        }
    }
}
