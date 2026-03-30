package com.example.seguimiento.features.FormularioDeAdopction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdoptionViewModel @Inject constructor() : ViewModel() {
    // Estado único para todo el formulario compartido entre pasos
    var state by mutableStateOf(AdoptionFormState())
        private set

    fun updateState(newState: AdoptionFormState) {
        state = newState
    }

    fun submitForm() {
        // Lógica para enviar a servidor
        println("Formulario enviado para: ${state.petName}")
    }
}
