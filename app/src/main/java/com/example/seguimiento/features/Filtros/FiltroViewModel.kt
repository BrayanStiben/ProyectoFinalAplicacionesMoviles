package com.example.seguimiento.features.Filtros

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.text.Normalizer
import javax.inject.Inject

@HiltViewModel
class FiltroViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    val currentUser = authRepository.currentUser

    // Estados de habilitación (Inician en true)
    var habilitarNombre by mutableStateOf(true)
    var habilitarTipo by mutableStateOf(true)
    var habilitarUbicacion by mutableStateOf(true)
    var habilitarEdad by mutableStateOf(true)

    // Valores de los filtros
    var nombreFiltro by mutableStateOf("")
    var tipoSeleccionado by mutableStateOf("Perro")
    var ubicacionFiltro by mutableStateOf("")
    var edadFiltro by mutableStateOf("")

    private val _resultados = MutableStateFlow<List<Mascota>>(emptyList())
    val resultados: StateFlow<List<Mascota>> = _resultados.asStateFlow()

    fun aplicarFiltros() {
        val todas = mascotaRepository.getAll()
        val filtradas = todas.filter { mascota ->
            val esVisible = mascota.estado == PublicacionEstado.VERIFICADA || 
                          mascota.estado == PublicacionEstado.RESUELTA ||
                          mascota.estado == PublicacionEstado.ADOPTADA

            val cumpleNombre = !habilitarNombre || 
                normalizar(mascota.nombre).contains(normalizar(nombreFiltro), ignoreCase = true)
            
            val cumpleTipo = !habilitarTipo || if (tipoSeleccionado == "Otro") {
                mascota.tipo != "Perro" && mascota.tipo != "Gato"
            } else {
                mascota.tipo.equals(tipoSeleccionado, ignoreCase = true)
            }

            val cumpleUbicacion = !habilitarUbicacion || 
                normalizar(mascota.ubicacion).contains(normalizar(ubicacionFiltro), ignoreCase = true)
            
            val cumpleEdad = !habilitarEdad || 
                normalizar(mascota.edad).contains(normalizar(edadFiltro), ignoreCase = true)

            esVisible && cumpleNombre && cumpleTipo && cumpleUbicacion && cumpleEdad
        }
        _resultados.value = filtradas
    }

    private fun normalizar(texto: String): String {
        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }

    fun limpiarFiltros() {
        habilitarNombre = true
        habilitarTipo = true
        habilitarUbicacion = true
        habilitarEdad = true
        nombreFiltro = ""
        tipoSeleccionado = "Perro"
        ubicacionFiltro = ""
        edadFiltro = ""
        _resultados.value = emptyList()
    }
}
