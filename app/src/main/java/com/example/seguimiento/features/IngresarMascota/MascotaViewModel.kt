package com.example.seguimiento.features.IngresarMascota

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MascotaViewModel @Inject constructor() : ViewModel() {
    private val _estado = MutableStateFlow(EstadoFormularioMascota())
    val estado: StateFlow<EstadoFormularioMascota> = _estado.asStateFlow()

    // Funciones para actualizar cada campo
    fun cambiarNombre(nuevoNombre: String) = _estado.update { it.copy(nombre = nuevoNombre) }
    fun cambiarTipo(nuevoTipo: String) = _estado.update { it.copy(tipo = nuevoTipo) }
    fun cambiarRaza(nuevaRaza: String) = _estado.update { it.copy(raza = nuevaRaza) }
    fun cambiarEdad(nuevaEdad: String) = _estado.update { it.copy(edad = nuevaEdad) }
    fun cambiarUnidadEdad(nuevaUnidad: String) = _estado.update { it.copy(unidadEdad = nuevaUnidad) }
    fun cambiarSexo(nuevoSexo: String) = _estado.update { it.copy(sexo = nuevoSexo) }
    fun cambiarCiudad(nuevaCiudad: String) = _estado.update { it.copy(ciudad = nuevaCiudad) }

    // Función para recibir la imagen de la galería
    fun alSeleccionarFoto(uri: Uri?) {
        _estado.update { it.copy(fotoUri = uri) }
    }

    fun guardarMascota() {
        val datos = _estado.value
        // Lógica para procesar o enviar los datos
        println("Mascota guardada: ${datos.nombre}")
    }
}
