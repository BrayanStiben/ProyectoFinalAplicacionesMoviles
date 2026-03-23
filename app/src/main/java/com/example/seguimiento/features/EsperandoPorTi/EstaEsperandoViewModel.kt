package com.example.seguimiento.features.EsperandoPorTi
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EstaEsperandoViewModel : ViewModel() {
    private val _mascotaDestacada = MutableStateFlow(
        Mascota(
            nombre = "Milo",
            edad = "3 meses",
            ubicacion = "Guadalajara",
            imagenUrl = "https://images.unsplash.com/photo-1593134257782-e89567b7718a?auto=format&fit=crop&w=300"
        )
    )
    val mascotaDestacada = _mascotaDestacada.asStateFlow()

    // Estado para la barra de navegación
    private val _tabSeleccionada = MutableStateFlow(0)
    val tabSeleccionada = _tabSeleccionada.asStateFlow()

    fun actualizarTab(indice: Int) {
        _tabSeleccionada.value = indice
    }

    fun ejecutarBusqueda() {
        println("Buscando mascotas...")
    }
}