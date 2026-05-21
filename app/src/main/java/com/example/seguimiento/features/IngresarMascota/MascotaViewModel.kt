package com.example.seguimiento.features.IngresarMascota

import android.content.Context
import android.location.Geocoder
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.UUID
import javax.inject.Inject
import com.example.seguimiento.R
import java.util.Locale

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
    private val aiService: AIService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoFormularioMascota())
    val estado: StateFlow<EstadoFormularioMascota> = _estado.asStateFlow()

    val currentUser = authRepository.currentUser
    private var idEdicion: String? = null

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
                    lat = mascota.lat,
                    lng = mascota.lng
                ) }
                
                if (depto.isNotEmpty()) {
                    cambiarDepartamento(depto)
                    _estado.update { it.copy(ciudad = ciudad) }
                }
            }
        }
    }

    // NUEVO: Método para inicializar coordenadas desde el Mapa
    fun inicializarConCoordenadas(lat: Double, lng: Double) {
        viewModelScope.launch {
            _estado.update { it.copy(lat = lat, lng = lng, isLoading = true) }
            
            val infoUbi = obtenerInfoDesdeCoords(lat, lng)
            
            // Si Geocoder encuentra el lugar, intentamos pre-seleccionar los dropdowns
            if (infoUbi != null) {
                val ciudad = infoUbi.first
                val depto = infoUbi.second
                
                _estado.update { it.copy(
                    departamento = depto,
                    ciudad = ciudad,
                    isLoading = false
                ) }
            } else {
                _estado.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun obtenerInfoDesdeCoords(lat: Double, lng: Double): Pair<String, String>? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val resultados = geocoder.getFromLocation(lat, lng, 1)
            if (!resultados.isNullOrEmpty()) {
                val loc = resultados[0]
                val ciudad = loc.locality ?: loc.subAdminArea ?: "Desconocido"
                val depto = loc.adminArea ?: "Desconocido"
                return@withContext Pair(ciudad, depto)
            }
        } catch (e: Exception) { e.printStackTrace() }
        return@withContext null
    }

    fun cambiarDepartamento(depto: String) {
        val deptoId = allDepartments.find { it.name == depto }?.id
        val ciudadesFiltradas = allCities.filter { it.departmentId == deptoId }.map { it.municipio ?: "" }.sorted()
        _estado.update { it.copy(departamento = depto, ciudad = "", lat = 0.0, lng = 0.0, listaCiudades = ciudadesFiltradas) }
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
    
    fun cambiarCiudad(nuevaCiudad: String) {
        val depto = _estado.value.departamento
        val deptoId = allDepartments.find { it.name.equals(depto, ignoreCase = true) }?.id
        
        val ciudadObj = allCities.find { 
            it.municipio?.trim()?.equals(nuevaCiudad.trim(), ignoreCase = true) == true && 
            it.departmentId == deptoId 
        }
        
        var apiLat = ciudadObj?.latitude ?: 0.0
        var apiLng = ciudadObj?.longitude ?: 0.0

        viewModelScope.launch {
            _estado.update { it.copy(ciudad = nuevaCiudad, isLoading = true) }
            
            if (apiLat == 0.0 || apiLng == 0.0) {
                val coords = obtenerCoordenadasDeRespaldo(nuevaCiudad, depto)
                apiLat = coords.first
                apiLng = coords.second
            }

            _estado.update { it.copy(
                lat = apiLat,
                lng = apiLng,
                isLoading = false,
                aiWarning = if (apiLat == 0.0) "No se pudieron obtener coordenadas. Prueba con otra ciudad." else null
            ) }
        }
    }

    private suspend fun obtenerCoordenadasDeRespaldo(ciudad: String, depto: String): Pair<Double, Double> = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val direccion = "$ciudad, $depto, Colombia"
            val resultados = geocoder.getFromLocationName(direccion, 1)
            if (!resultados.isNullOrEmpty()) {
                val loc = resultados[0]
                return@withContext Pair(loc.latitude, loc.longitude)
            }
        } catch (e: Exception) { e.printStackTrace() }
        return@withContext Pair(0.0, 0.0)
    }

    fun cambiarDescripcion(nuevaDesc: String) = _estado.update { it.copy(descripcion = nuevaDesc) }
    fun alSeleccionarFoto(uri: Uri?) = _estado.update { it.copy(fotoUri = uri) }

    fun guardarMascota(onSuccess: () -> Unit) {
        val datos = _estado.value
        
        if (datos.lat == 0.0 || datos.lng == 0.0) {
            _estado.update { it.copy(aiWarning = "Error: Ubicación sin coordenadas GPS.") }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(isLoading = true, aiWarning = null) }

            val currentUser = authRepository.currentUser.value
            val userId = currentUser?.id ?: "1"
            val currentUserName = currentUser?.name ?: "Usuario Desconocido"
            val esAdmin = currentUser?.role == UserRole.ADMIN

            val mascotaExistente = idEdicion?.let { mascotaRepository.getById(it) }

            // Creamos objeto temporal para validar con IA
            val mascotaTemp = Mascota(
                nombre = datos.nombre,
                descripcion = datos.descripcion,
                tipo = datos.tipo,
                raza = datos.raza,
                ubicacion = "${datos.ciudad}, ${datos.departamento}"
            )

            // Validación Inteligente con IA
            val validacionIA = aiService.validarPublicacion(mascotaTemp)

            // REGLA: Toda publicación de usuario queda PENDIENTE para revisión del Admin, 
            // incluso si la IA detecta insultos (el Admin decide final).
            val estadoFinal = if (esAdmin) PublicacionEstado.VERIFICADA else PublicacionEstado.PENDIENTE

            val mascotaParaGuardar = Mascota(
                id = idEdicion ?: UUID.randomUUID().toString(),
                nombre = datos.nombre,
                edad = "${datos.edad} ${datos.unidadEdad}",
                tipo = datos.tipo,
                raza = datos.raza,
                ubicacion = "${datos.ciudad}, ${datos.departamento}",
                descripcion = datos.descripcion,
                imagenUrl = mascotaExistente?.imagenUrl ?: "",
                lat = datos.lat,
                lng = datos.lng,
                autorId = mascotaExistente?.autorId ?: userId,
                autorNombre = if (mascotaExistente?.autorNombre.isNullOrBlank()) currentUserName else mascotaExistente!!.autorNombre,
                estado = estadoFinal,
                resumenIA = validacionIA.feedback,
                iaEsValida = validacionIA.isValid && !validacionIA.isOffensive,
                motivoRechazo = if (validacionIA.isOffensive) context.getString(R.string.ai_inappropriate_content_warning) else "",
                likerIds = mascotaExistente?.likerIds ?: emptyList()
            )

            mascotaRepository.save(mascotaParaGuardar, datos.fotoUri)

            // Si es ofensivo, generamos alerta para el administrador
            if (validacionIA.isOffensive) {
                notificacionRepository.addNotificacion(
                    tituloResId = R.string.ai_moderation_alert_title,
                    mensajeResId = R.string.ai_moderation_alert_msg,
                    mensajeArgs = listOf(datos.nombre, validacionIA.feedback),
                    tipo = "ADMIN_ALERT",
                    userId = "admin_id" // En un sistema real, esto iría a todos los admins
                )
            }

            onSuccess()
        }
    }
}
