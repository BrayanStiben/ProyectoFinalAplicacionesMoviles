package com.example.seguimiento.features.Filtros

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.features.FinalizarRegistro.CityResponse
import com.example.seguimiento.features.FinalizarRegistro.ColombiaApiService
import com.example.seguimiento.features.FinalizarRegistro.DepartmentResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.Normalizer
import javax.inject.Inject

@HiltViewModel
class FiltroViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    val currentUser = authRepository.currentUser

    // --- API COLOMBIA ---
    private val retrofitColombia = Retrofit.Builder()
        .baseUrl("https://api-colombia.com/api/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiColombia = retrofitColombia.create(ColombiaApiService::class.java)

    var listaDepartamentos by mutableStateOf<List<String>>(emptyList())
    var listaCiudades by mutableStateOf<List<String>>(emptyList())
    private var allDepartments = listOf<DepartmentResponse>()
    private var allCities = listOf<CityResponse>()

    // Estados de habilitación
    var habilitarNombre by mutableStateOf(false)
    var habilitarTipo by mutableStateOf(false)
    var habilitarUbicacion by mutableStateOf(false)
    var habilitarEdad by mutableStateOf(false)

    // Valores de los filtros
    var nombreFiltro by mutableStateOf("")
    var tipoSeleccionado by mutableStateOf("Perro")
    var departamentoSeleccionado by mutableStateOf("")
    var ciudadSeleccionada by mutableStateOf("")
    var edadFiltro by mutableStateOf("")

    private val _resultados = MutableStateFlow<List<Mascota>>(emptyList())
    val resultados: StateFlow<List<Mascota>> = _resultados.asStateFlow()

    init {
        cargarUbicaciones()
    }

    private fun cargarUbicaciones() {
        viewModelScope.launch {
            try {
                allDepartments = apiColombia.getDepartamentos()
                allCities = apiColombia.getMunicipios()
                listaDepartamentos = allDepartments.map { it.name ?: "" }.sorted()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun cambiarDepartamento(depto: String) {
        departamentoSeleccionado = depto
        ciudadSeleccionada = ""
        val deptoId = allDepartments.find { it.name == depto }?.id
        listaCiudades = allCities.filter { it.departmentId == deptoId }.map { it.municipio ?: "" }.sorted()
    }

    fun aplicarFiltros() {
        val todas = mascotaRepository.getAll()
        val filtradas = todas.filter { mascota ->
            val esVisible = mascota.estado == PublicacionEstado.VERIFICADA || 
                          mascota.estado == PublicacionEstado.RESUELTA ||
                          mascota.estado == PublicacionEstado.ADOPTADA

            val cumpleNombre = !habilitarNombre || 
                normalizar(mascota.nombre).contains(normalizar(nombreFiltro), ignoreCase = true)
            
            val cumpleTipo = !habilitarTipo || when {
                tipoSeleccionado == "Otro" || tipoSeleccionado == "Other" -> {
                    mascota.tipo != "Perro" && mascota.tipo != "Gato" && 
                    mascota.tipo != "Dog" && mascota.tipo != "Cat"
                }
                tipoSeleccionado == "Perro" || tipoSeleccionado == "Dog" -> {
                    mascota.tipo.equals("Perro", ignoreCase = true) || mascota.tipo.equals("Dog", ignoreCase = true)
                }
                tipoSeleccionado == "Gato" || tipoSeleccionado == "Cat" -> {
                    mascota.tipo.equals("Gato", ignoreCase = true) || mascota.tipo.equals("Cat", ignoreCase = true)
                }
                else -> {
                    mascota.tipo.equals(tipoSeleccionado, ignoreCase = true)
                }
            }

            val cumpleUbicacion = !habilitarUbicacion || (
                (departamentoSeleccionado.isEmpty() || mascota.ubicacion.contains(departamentoSeleccionado, ignoreCase = true)) &&
                (ciudadSeleccionada.isEmpty() || mascota.ubicacion.contains(ciudadSeleccionada, ignoreCase = true))
            )
            
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
        habilitarNombre = false
        habilitarTipo = false
        habilitarUbicacion = false
        habilitarEdad = false
        nombreFiltro = ""
        tipoSeleccionado = "Perro"
        departamentoSeleccionado = ""
        ciudadSeleccionada = ""
        edadFiltro = ""
        _resultados.value = emptyList()
    }
}
