package com.example.seguimiento.features.FinalizarRegistro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@HiltViewModel
class FinalizarRegistroViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api-colombia.com/api/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ColombiaApiService::class.java)

    private val _departamentos = MutableStateFlow<List<DepartmentResponse>>(emptyList())
    val departamentos = _departamentos.map { list -> list.map { it.name ?: "" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _municipios = MutableStateFlow<List<CityResponse>>(emptyList())
    
    private val _deptoSeleccionado = MutableStateFlow("")
    val deptoSeleccionado = _deptoSeleccionado.asStateFlow()

    private val _municipioSeleccionado = MutableStateFlow("")
    val municipioSeleccionado = _municipioSeleccionado.asStateFlow()

    private val _municipiosFiltrados = MutableStateFlow<List<String>>(emptyList())
    val municipiosFiltrados = _municipiosFiltrados.asStateFlow()

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
                val deptos = apiService.getDepartamentos()
                _departamentos.value = deptos
                
                val munis = apiService.getMunicipios()
                _municipios.value = munis
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _estaCargando.value = false
            }
        }
    }

    fun onDepartamentoChanged(deptoNombre: String) {
        _deptoSeleccionado.value = deptoNombre
        _municipioSeleccionado.value = ""
        
        val deptoId = _departamentos.value.find { it.name == deptoNombre }?.id
        if (deptoId != null) {
            _municipiosFiltrados.value = _municipios.value
                .filter { it.departmentId == deptoId }
                .map { it.municipio ?: "" }
                .sorted()
        }
    }

    fun onMunicipioChanged(muni: String) {
        _municipioSeleccionado.value = muni
    }

    fun setTerminosAceptados(aceptado: Boolean) {
        _terminosAceptados.value = aceptado
    }

    fun finalizarRegistro(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _estaCargando.value = true
            val user = authRepository.currentUser.value
            if (user != null) {
                val updatedUser = user.copy(
                    departamento = _deptoSeleccionado.value,
                    city = _municipioSeleccionado.value
                )
                authRepository.register(updatedUser)
                onSuccess()
            }
            _estaCargando.value = false
        }
    }
}
