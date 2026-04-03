package com.example.seguimiento.features.IngresarMascota

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.modelos.UserRole
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.Dominio.servicios.AIService
import com.example.seguimiento.features.FinalizarRegistro.CityResponse
import com.example.seguimiento.features.FinalizarRegistro.ColombiaApiService
import com.example.seguimiento.features.FinalizarRegistro.DepartmentResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.UUID
import javax.inject.Inject

// --- API DE MASCOTAS ---
interface DogApiService {
    @GET("breeds/list/all")
    suspend fun getDogBreeds(): Map<String, Any>
}

interface CatApiService {
    @GET("breeds")
    suspend fun getCatBreeds(): List<CatBreed>
}

data class CatBreed(val name: String)

@HiltViewModel
class MascotaViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository,
    private val authRepository: AuthRepository,
    private val notificacionRepository: NotificacionRepository,
    private val aiService: AIService
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoFormularioMascota())
    val estado: StateFlow<EstadoFormularioMascota> = _estado.asStateFlow()

    val currentUser = authRepository.currentUser

    private var idEdicion: String? = null

    // --- RETROFITS ---
    private val retrofitColombia = Retrofit.Builder()
        .baseUrl("https://api-colombia.com/api/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiColombia = retrofitColombia.create(ColombiaApiService::class.java)

    private val retrofitDog = Retrofit.Builder()
        .baseUrl("https://dog.ceo/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiDog = retrofitDog.create(DogApiService::class.java)

    private val retrofitCat = Retrofit.Builder()
        .baseUrl("https://api.thecatapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiCat = retrofitCat.create(CatApiService::class.java)

    private var allDepartments = listOf<DepartmentResponse>()
    private var allCities = listOf<CityResponse>()

    init {
        cargarUbicaciones()
    }

    private fun cargarUbicaciones() {
        viewModelScope.launch {
            try {
                allDepartments = apiColombia.getDepartamentos()
                allCities = apiColombia.getMunicipios()
                _estado.update { it.copy(listaDepartamentos = allDepartments.map { d -> d.name ?: "" }) }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun cargarMascotaParaEdicion(id: String) {
        viewModelScope.launch {
            val mascota = mascotaRepository.getById(id)
            if (mascota != null) {
                idEdicion = id
                val partesUbi = mascota.ubicacion.split(", ")
                val ciudad = partesUbi.getOrNull(0) ?: ""
                val depto = partesUbi.getOrNull(1) ?: ""
                
                // Cargar razas para el tipo actual
                cargarRazas(mascota.tipo)
                
                _estado.update { it.copy(
                    nombre = mascota.nombre,
                    descripcion = mascota.descripcion,
                    tipo = mascota.tipo,
                    raza = mascota.raza,
                    edad = mascota.edad.split(" ").firstOrNull() ?: "",
                    unidadEdad = mascota.edad.split(" ").lastOrNull() ?: "años",
                    departamento = depto,
                    ciudad = ciudad,
                    fotoUri = if (mascota.imagenUrl.startsWith("http")) null else Uri.parse(mascota.imagenUrl),
                ) }
                
                if (depto.isNotEmpty()) {
                    cambiarDepartamento(depto)
                    _estado.update { it.copy(ciudad = ciudad) }
                }
            }
        }
    }

    fun cambiarDepartamento(depto: String) {
        val deptoId = allDepartments.find { it.name == depto }?.id
        val ciudadesFiltradas = allCities.filter { it.departmentId == deptoId }.map { it.municipio ?: "" }.sorted()
        _estado.update { it.copy(departamento = depto, ciudad = "", listaCiudades = ciudadesFiltradas) }
    }

    fun cambiarNombre(nuevoNombre: String) = _estado.update { it.copy(nombre = nuevoNombre) }
    
    fun cambiarTipo(nuevoTipo: String) {
        _estado.update { it.copy(tipo = nuevoTipo, raza = "", listaRazas = emptyList()) }
        cargarRazas(nuevoTipo)
    }

    private fun cargarRazas(tipo: String) {
        viewModelScope.launch {
            try {
                val cleanTipo = tipo.lowercase()
                    .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")

                when (cleanTipo) {
                    "perro" -> {
                        val response = apiDog.getDogBreeds()
                        val message = response["message"] as? Map<*, *>
                        val razasMap = message as? Map<String, List<String>>
                        val razas = razasMap?.keys?.toList()?.map { it.replaceFirstChar { c -> c.uppercase() } } ?: emptyList()
                        _estado.update { it.copy(listaRazas = razas) }
                    }
                    "gato" -> {
                        val razas = apiCat.getCatBreeds().map { it.name }
                        _estado.update { it.copy(listaRazas = razas) }
                    }
                    "pajaro" -> {
                        val razas = listOf("Canario", "Periquito", "Loro", "Cacatúa", "Diamante de Gould")
                        _estado.update { it.copy(listaRazas = razas) }
                    }
                    else -> _estado.update { it.copy(listaRazas = emptyList()) }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun cambiarRaza(nuevaRaza: String) = _estado.update { it.copy(raza = nuevaRaza) }
    fun cambiarEdad(nuevaEdad: String) = _estado.update { it.copy(edad = nuevaEdad) }
    fun cambiarUnidadEdad(nuevaUnidad: String) = _estado.update { it.copy(unidadEdad = nuevaUnidad) }
    fun cambiarSexo(nuevoSexo: String) = _estado.update { it.copy(sexo = nuevoSexo) }
    fun cambiarCiudad(nuevaCiudad: String) = _estado.update { it.copy(ciudad = nuevaCiudad) }
    fun cambiarDescripcion(nuevaDesc: String) = _estado.update { it.copy(descripcion = nuevaDesc) }

    fun alSeleccionarFoto(uri: Uri?) = _estado.update { it.copy(fotoUri = uri) }

    fun guardarMascota(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val datos = _estado.value
            _estado.update { it.copy(isLoading = true, aiWarning = null) }

            val currentUser = authRepository.currentUser.value
            val userId = currentUser?.id ?: "1"
            val esAdmin = currentUser?.role == UserRole.ADMIN

            val advertenciaNombre = aiService.analizarContenido(datos.nombre)
            val advertenciaDesc = aiService.analizarContenido(datos.descripcion)

            if (advertenciaNombre != null || advertenciaDesc != null) {
                _estado.update { it.copy(isLoading = false, aiWarning = advertenciaNombre ?: advertenciaDesc) }
                return@launch
            }

            val resumen = aiService.generarResumen(datos.descripcion, datos.tipo)

            val mascotaExistente = idEdicion?.let { mascotaRepository.getById(it) }
            
            val mascotaParaGuardar = Mascota(
                id = idEdicion ?: UUID.randomUUID().toString(),
                nombre = datos.nombre,
                edad = "${datos.edad} ${datos.unidadEdad}",
                tipo = datos.tipo,
                raza = datos.raza,
                ubicacion = "${datos.ciudad}, ${datos.departamento}",
                descripcion = datos.descripcion,
                imagenUrl = datos.fotoUri?.toString() ?: mascotaExistente?.imagenUrl ?: "",
                lat = datos.lat,
                lng = datos.lng,
                autorId = mascotaExistente?.autorId ?: userId,
                estado = mascotaExistente?.estado ?: if (esAdmin) PublicacionEstado.VERIFICADA else PublicacionEstado.PENDIENTE,
                resumenIA = resumen,
                likerIds = mascotaExistente?.likerIds ?: emptyList()
            )

            mascotaRepository.save(mascotaParaGuardar)

            // NOTIFICACIÓN DE CREACIÓN/EDICIÓN
            if (idEdicion == null) {
                notificacionRepository.addNotificacion(
                    titulo = "¡Mascota registrada! 🐾",
                    mensaje = "Has registrado a ${datos.nombre} con éxito. Un administrador revisará la publicación pronto.",
                    tipo = "INFO",
                    userId = userId
                )
            } else {
                notificacionRepository.addNotificacion(
                    titulo = "¡Mascota actualizada! ✨",
                    mensaje = "Los cambios en la información de ${datos.nombre} se han guardado.",
                    tipo = "INFO",
                    userId = userId
                )
            }

            _estado.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }
}
