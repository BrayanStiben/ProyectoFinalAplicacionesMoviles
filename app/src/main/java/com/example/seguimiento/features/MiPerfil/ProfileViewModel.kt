package com.example.seguimiento.features.MiPerfil

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    // Estados de texto
    private val _nombre = MutableStateFlow("")
    val nombre = _nombre.asStateFlow()

    private val _apellido = MutableStateFlow("")
    val apellido = _apellido.asStateFlow()

    private val _ubicacion = MutableStateFlow("Ubicación Actual")
    val ubicacion = _ubicacion.asStateFlow()

    // Estado de la foto (Galería)
    private val _fotoUri = MutableStateFlow<Uri?>(null)
    val fotoUri = _fotoUri.asStateFlow()

    // Estado de navegación
    private val _itemSeleccionado = MutableStateFlow(3)
    val itemSeleccionado = _itemSeleccionado.asStateFlow()

    // Checkboxes para tipo de mascota
    private val _esPerro = MutableStateFlow(true)
    val esPerro = _esPerro.asStateFlow()

    private val _esGato = MutableStateFlow(false)
    val esGato = _esGato.asStateFlow()

    private val _esOtro = MutableStateFlow(false)
    val esOtro = _esOtro.asStateFlow()

    private val _otroTexto = MutableStateFlow("")
    val otroTexto = _otroTexto.asStateFlow()

    // Funciones de actualización
    fun actualizarNombre(n: String) { _nombre.value = n }
    fun actualizarApellido(a: String) { _apellido.value = a }
    fun actualizarFoto(uri: Uri?) { _fotoUri.value = uri }
    fun cambiarNavegacion(i: Int) { _itemSeleccionado.value = i }

    fun togglePerro(b: Boolean) {
        _esPerro.value = b
        if (b) {
            _esGato.value = false
            _esOtro.value = false
        }
    }

    fun toggleGato(b: Boolean) {
        _esGato.value = b
        if (b) {
            _esPerro.value = false
            _esOtro.value = false
        }
    }

    fun toggleOtro(b: Boolean) {
        _esOtro.value = b
        if (b) {
            _esPerro.value = false
            _esGato.value = false
        }
    }

    fun actualizarOtroTexto(t: String) { _otroTexto.value = t }

    fun guardarCambios() {
        val tipo = when {
            _esPerro.value -> "Perro"
            _esGato.value -> "Gato"
            _esOtro.value -> "Otro: ${_otroTexto.value}"
            else -> "No especificado"
        }
        println("Guardando: ${_nombre.value} - Mascota: $tipo en ${_ubicacion.value}")
    }

    @SuppressLint("MissingPermission")
    fun obtenerUbicacionActual(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        // Intentar obtener la última ubicación conocida
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _ubicacion.value = "${location.latitude}, ${location.longitude}"
            } else {
                // Si la última ubicación es nula, solicitar la ubicación actual de forma activa
                val priority = Priority.PRIORITY_HIGH_ACCURACY
                val cancellationTokenSource = CancellationTokenSource()
                
                fusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
                    .addOnSuccessListener { currentLocation ->
                        if (currentLocation != null) {
                            _ubicacion.value = "${currentLocation.latitude}, ${currentLocation.longitude}"
                        } else {
                            _ubicacion.value = "No se pudo obtener la ubicación"
                        }
                    }
            }
        }.addOnFailureListener {
            _ubicacion.value = "Error al obtener ubicación"
        }
    }
}
