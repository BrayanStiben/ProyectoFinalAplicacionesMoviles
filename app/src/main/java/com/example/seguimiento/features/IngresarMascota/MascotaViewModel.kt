package com.example.seguimiento.features.IngresarMascota

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.servicios.AIService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MascotaViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository,
    private val aiService: AIService
) : ViewModel() {
    private val _estado = MutableStateFlow(EstadoFormularioMascota())
    val estado: StateFlow<EstadoFormularioMascota> = _estado.asStateFlow()

    fun cambiarNombre(nuevoNombre: String) = _estado.update { it.copy(nombre = nuevoNombre) }
    fun cambiarTipo(nuevoTipo: String) = _estado.update { it.copy(tipo = nuevoTipo) }
    fun cambiarRaza(nuevaRaza: String) = _estado.update { it.copy(raza = nuevaRaza) }
    fun cambiarEdad(nuevaEdad: String) = _estado.update { it.copy(edad = nuevaEdad) }
    fun cambiarUnidadEdad(nuevaUnidad: String) = _estado.update { it.copy(unidadEdad = nuevaUnidad) }
    fun cambiarSexo(nuevoSexo: String) = _estado.update { it.copy(sexo = nuevoSexo) }
    fun cambiarCiudad(nuevaCiudad: String) = _estado.update { it.copy(ciudad = nuevaCiudad) }
    fun cambiarDescripcion(nuevaDesc: String) = _estado.update { it.copy(descripcion = nuevaDesc) }

    fun alSeleccionarFoto(uri: Uri?) {
        _estado.update { it.copy(fotoUri = uri) }
    }

    fun guardarMascota(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val datos = _estado.value
            _estado.update { it.copy(isLoading = true, aiWarning = null) }

            // Requisito IA 3: Detección de contenido inapropiado
            val advertenciaNombre = aiService.analizarContenido(datos.nombre)
            val advertenciaDesc = aiService.analizarContenido(datos.descripcion)

            if (advertenciaNombre != null || advertenciaDesc != null) {
                _estado.update { it.copy(
                    isLoading = false,
                    aiWarning = advertenciaNombre ?: advertenciaDesc
                ) }
                return@launch
            }

            // Generar resumen para el moderador (Requisito IA 4)
            val resumen = aiService.generarResumen(datos.descripcion, datos.tipo)

            val nuevaMascota = Mascota(
                id = UUID.randomUUID().toString(),
                nombre = datos.nombre,
                edad = "${datos.edad} ${datos.unidadEdad}",
                tipo = datos.tipo,
                raza = datos.raza,
                ubicacion = datos.ciudad,
                descripcion = datos.descripcion,
                imagenUrl = datos.fotoUri?.toString() ?: "",
                lat = datos.lat,
                lng = datos.lng,
                estado = PublicacionEstado.PENDIENTE,
                resumenIA = resumen
            )

            mascotaRepository.save(nuevaMascota)
            _estado.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }
}
