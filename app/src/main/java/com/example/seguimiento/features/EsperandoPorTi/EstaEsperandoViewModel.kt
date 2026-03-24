package com.example.seguimiento.features.EsperandoPorTi
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EstaEsperandoViewModel : ViewModel() {
    private val _mascotas = MutableStateFlow(
        listOf(
            Mascota("Luna", "2 años", "CDMX", "https://images.dog.ceo/breeds/husky/n02110185_1469.jpg"),
            Mascota("Feliz Catus", "3 meses", "Gdl", "https://placecats.com/neo/300/200"),
            Mascota("Bella", "1 año", "Mty", "https://images.dog.ceo/breeds/retriever-golden/n02099601_3004.jpg")
        )
    )
    val mascotas = _mascotas.asStateFlow()

    private val _mascotaDestacada = MutableStateFlow(
        Mascota(
            nombre = "Luna",
            edad = "2 años",
            ubicacion = "CDMX",
            imagenUrl = "https://images.dog.ceo/breeds/husky/n02110185_1469.jpg"
        )
    )
    val mascotaDestacada = _mascotaDestacada.asStateFlow()

    // Estado para la barra de navegación
    private val _tabSeleccionada = MutableStateFlow(0)
    val tabSeleccionada = _tabSeleccionada.asStateFlow()

    fun actualizarTab(indice: Int) {
        _tabSeleccionada.value = indice
    }

    fun seleccionarMascota(mascota: Mascota) {
        _mascotaDestacada.value = mascota
    }

    fun ejecutarBusqueda() {
        println("Buscando mascotas...")
    }
}
