package com.example.seguimiento.features.Filtros
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.compose.runtime.*

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
// --- COLORES PERSONALIZADOS ---

val AzulGps = Color(0xFF2196F3)

// --- VIEWMODEL (LÓGICA DE ESTADO) ---
class FiltroViewModel : ViewModel() {
    var tipoMascotaSeleccionado by mutableStateOf("Perros")
    var razaSeleccionada by mutableStateOf("")
    var categoriaEdad by mutableStateOf("Cachorro")

    // Peso: Rango que garantiza valores positivos (mínimo 1kg)
    var rangoPeso by mutableStateOf(1f..15f)

    // GPS: Radio de búsqueda
    var radioGps by mutableStateOf(10f)

    var estaVacunado by mutableStateOf(true)
    var estaEsterilizado by mutableStateOf(true)
    var esJugueton by mutableStateOf(false)
    var palabrasClave by mutableStateOf("")

    fun actualizarRangoPeso(nuevoRango: ClosedFloatingPointRange<Float>) {
        // Validación para que el inicio nunca sea menor a 1 (Siempre positivo)
        val inicioSeguro = if (nuevoRango.start < 1f) 1f else nuevoRango.start
        rangoPeso = inicioSeguro..nuevoRango.endInclusive
    }

    fun limpiarFiltros() {
        tipoMascotaSeleccionado = "Perros"
        razaSeleccionada = ""
        categoriaEdad = "Cachorro"
        rangoPeso = 1f..15f
        radioGps = 10f
        estaVacunado = false
        estaEsterilizado = false
        esJugueton = false
    }
}