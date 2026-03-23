package com.example.seguimiento.features.QueNesecitoParaAdoptar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SeccionAdopcion { TUS_DATOS, CALCULAR_COMIDA, HOGAR_FELIZ }

class QueNesecitoParaAdoptarViewModel : ViewModel() {

    private val _seccionSeleccionada = MutableStateFlow<SeccionAdopcion?>(null)
    val seccionSeleccionada = _seccionSeleccionada.asStateFlow()

    private val _tabSeleccionada = MutableStateFlow(0)
    val tabSeleccionada = _tabSeleccionada.asStateFlow()

    val contenidosPopUp = mapOf(
        SeccionAdopcion.TUS_DATOS to """
            DETALLES DE TUS DATOS:
            1. Documento de identidad (Cédula/DNI).
            2. Comprobante de dirección actual.
            3. Referencias personales (2 personas).
            4. Edad mínima de 21 años.
        """.trimIndent(),

        SeccionAdopcion.CALCULAR_COMIDA to """
            CALCULAR COMIDA DE ANIMAL:
            Es fundamental conocer la cantidad exacta de alimento que tu mascota necesita para mantenerse saludable y con energía. 
            
            Usa nuestra calculadora recomendada para obtener un plan nutricional a medida o adquiere el mejor alimento para tu nuevo amigo.
        """.trimIndent(),

        SeccionAdopcion.HOGAR_FELIZ to """
            REQUISITOS DEL HOGAR:
            1. Espacio seguro y cercado.
            2. Área techada para descanso.
            3. Aceptación de todos los habitantes.
            4. Tiempo diario para ejercicio y juegos.
        """.trimIndent()
    )

    fun abrirPopUp(seccion: SeccionAdopcion) {
        _seccionSeleccionada.value = seccion
    }

    fun cerrarPopUp() {
        _seccionSeleccionada.value = null
    }

    fun cambiarTab(indice: Int) {
        _tabSeleccionada.value = indice
    }
}