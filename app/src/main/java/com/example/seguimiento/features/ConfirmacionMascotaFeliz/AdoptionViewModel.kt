package com.example.seguimiento.features.ConfirmacionMascotaFeliz

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AdoptionViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AdoptionUiState())
    val uiState: StateFlow<AdoptionUiState> = _uiState.asStateFlow()

    fun onHistoryClick() {
        // Lógica para navegar o mostrar historia
    }

    fun onShareClick() {
        // Lógica para compartir
    }
}
