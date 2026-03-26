package com.example.seguimiento.features.FinalizarRegistro

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class FinalizarRegistroViewModel @Inject constructor() : ViewModel() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl("https://api-colombia.com/api/v1/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ColombiaApiService::class.java)

    // Almacenamos los datos crudos
    private var todasLasCiudades = listOf<CityResponse>()
    private var todosLosDeptos = listOf<DepartmentResponse>()

    // Estados para la UI
    private val _departamentos = MutableStateFlow<List<String>>(emptyList())
    val departamentos = _departamentos.asStateFlow()

    private val _municipiosFiltrados = MutableStateFlow<List<String>>(emptyList())
    val municipiosFiltrados = _municipiosFiltrados.asStateFlow()

    private val _deptoSeleccionado = MutableStateFlow("")
    val deptoSeleccionado = _deptoSeleccionado.asStateFlow()

    private val _municipioSeleccionado = MutableStateFlow("")
    val municipioSeleccionado = _municipioSeleccionado.asStateFlow()

    private val _terminosAceptados = MutableStateFlow(false)
    val terminosAceptados = _terminosAceptados.asStateFlow()

    private val _estaCargando = MutableStateFlow(false)
    val estaCargando = _estaCargando.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            _estaCargando.value = true
            try {
                Log.d("API_COLOMBIA", "Descargando Departamentos y Ciudades...")
                
                // Descargamos ambos en paralelo para mayor velocidad
                val deptosResponse = api.getDepartamentos()
                val citiesResponse = api.getMunicipios()

                if (deptosResponse.isNotEmpty()) {
                    todosLosDeptos = deptosResponse
                    todasLasCiudades = citiesResponse
                    
                    Log.d("API_COLOMBIA", "¡Éxito! ${todosLosDeptos.size} deptos y ${todasLasCiudades.size} ciudades.")

                    _departamentos.value = deptosResponse
                        .mapNotNull { it.name?.uppercase()?.trim() }
                        .distinct()
                        .sorted()
                }
            } catch (e: Exception) {
                Log.e("API_COLOMBIA", "Error: ${e.message}")
                // Respaldo manual mínimo
                _departamentos.value = listOf("ANTIOQUIA", "BOGOTÁ D.C.", "VALLE DEL CAUCA")
            } finally {
                _estaCargando.value = false
            }
        }
    }

    fun onDepartamentoChanged(nombreDepto: String) {
        _deptoSeleccionado.value = nombreDepto
        _municipioSeleccionado.value = ""
        
        // Buscamos el ID del departamento seleccionado para filtrar las ciudades
        val deptoId = todosLosDeptos.find { it.name?.uppercase()?.trim() == nombreDepto }?.id

        if (deptoId != null) {
            _municipiosFiltrados.value = todasLasCiudades
                .filter { it.departmentId == deptoId }
                .mapNotNull { it.municipio?.uppercase()?.trim() }
                .distinct()
                .sorted()
            Log.d("API_COLOMBIA", "Filtrados ${_municipiosFiltrados.value.size} municipios para $nombreDepto")
        } else {
            _municipiosFiltrados.value = emptyList()
        }
    }

    fun onMunicipioChanged(muni: String) {
        _municipioSeleccionado.value = muni
    }

    fun setTerminosAceptados(valor: Boolean) {
        _terminosAceptados.value = valor
    }
}
