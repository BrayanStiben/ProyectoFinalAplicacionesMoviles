package com.example.seguimiento.features.ListaDeSolicitudes

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

 import com.example.seguimiento.features.EncontrarMascotas.Mascota

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AdopcionViewModel : ViewModel() {
    private val _solicitudes = MutableStateFlow<List<DatosMascota>>(emptyList())
    val solicitudes = _solicitudes.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        _solicitudes.value = listOf(
            DatosMascota(1, "Luna", "Pendiente", "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=500", progreso = 0.7f),
            DatosMascota(2, "Milo", "Entrevista Programada", "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=500", subtexto = "29/03/2023 - 8:00 PM"),
            DatosMascota(3, "Bella", "¡Aprobado!", "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?w=500", subtexto = "(Ver Pasos Siguientes)", esAprobado = true)
        )
    }

    fun contactarMascota(id: Int) {
        _solicitudes.update { lista ->
            lista.map { mascota ->
                if (mascota.id == id) {
                    // Actualizamos a "Contactado" y el color cambiará a azul en la UI
                    mascota.copy(
                        estado = "Contactado",
                        subtexto = "¡Mensaje enviado!",
                        progreso = null,
                        esAprobado = false
                    )
                } else mascota
            }
        }
    }
}