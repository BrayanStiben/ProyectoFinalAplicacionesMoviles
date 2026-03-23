package com.example.seguimiento.features.EncontrarMascotas

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class AdopcionViewModel : ViewModel() {
    private val _listaMascotas = MutableStateFlow(
        listOf(
            Mascota("Luna", "2 años", "Murcia", "Muy juguetona y cariñosa.", "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?q=80&w=600"),
            Mascota("Thor", "3 años", "Madrid", "Leal, protector y muy tranquilo.", "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?q=80&w=600"),
            Mascota("Simba", "1 año", "Barcelona", "Un gatito curioso que ama saltar.", "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=600"),
            Mascota("Nala", "4 años", "Valencia", "Dulce y perfecta para una familia.", "https://images.unsplash.com/photo-1534361960057-19889db9621e?q=80&w=600"),
            Mascota("Bobi", "5 meses", "Sevilla", "Cachorro con mucha energía.", "https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=600"),
            Mascota("Kira", "2 años", "Galicia", "Le encanta correr por el campo.", "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?q=80&w=600")
        )
    )
    val listaMascotas: StateFlow<List<Mascota>> = _listaMascotas

    fun adoptar(nombre: String) {
        _listaMascotas.value = _listaMascotas.value.map {
            if (it.nombre == nombre) it.copy(esAdoptada = true) else it
        }
    }
}