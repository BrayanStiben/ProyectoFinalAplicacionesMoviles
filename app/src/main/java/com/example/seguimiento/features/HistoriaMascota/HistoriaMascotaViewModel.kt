package com.example.seguimiento.features.HistoriaMascota

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HistoriaMascotaViewModel @Inject constructor() : ViewModel() {

    private val _textoHistoria = MutableStateFlow("")
    val textoHistoria: StateFlow<String> = _textoHistoria.asStateFlow()

    val fotosCarrusel = listOf(
        "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=500",
        "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?q=80&w=500",
        "https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?q=80&w=500",
        "https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=500",
        "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?q=80&w=500"
    )

    fun alCambiarTexto(nuevoTexto: String) {
        _textoHistoria.value = nuevoTexto
    }

    fun compartir() {
        println("Historia compartida: ${_textoHistoria.value}")
    }
}
