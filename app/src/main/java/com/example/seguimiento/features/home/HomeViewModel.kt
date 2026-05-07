package com.example.seguimiento.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Logro
import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.Notificacion
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.LogrosRepository
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.R
import com.example.seguimiento.features.FinalizarRegistro.CityResponse
import com.example.seguimiento.features.FinalizarRegistro.ColombiaApiService
import com.example.seguimiento.features.FinalizarRegistro.DepartmentResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val mascotaRepository: MascotaRepository,
    private val notificacionRepository: NotificacionRepository,
    private val logrosRepository: LogrosRepository
) : ViewModel() {

    private val retrofitColombia = Retrofit.Builder()
        .baseUrl("https://api-colombia.com/api/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiColombia = retrofitColombia.create(ColombiaApiService::class.java)

    private val _listaDepartamentos = MutableStateFlow<List<String>>(emptyList())
    val listaDepartamentos = _listaDepartamentos.asStateFlow()

    private val _listaCiudades = MutableStateFlow<List<String>>(emptyList())
    val listaCiudades = _listaCiudades.asStateFlow()

    private var allDepartments = listOf<DepartmentResponse>()
    private var allCities = listOf<CityResponse>()

    val currentUser = authRepository.currentUser

    val userName: StateFlow<String> = authRepository.currentUser
        .map { user -> user?.name ?: "Usuario" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Usuario")

    val userProfilePicture: StateFlow<String?> = authRepository.currentUser
        .map { user -> user?.profilePictureUrl }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val notificaciones: StateFlow<List<Notificacion>> = notificacionRepository.notificaciones

    val todosLosLogros: List<Logro> = logrosRepository.todosLosLogros
    
    val logrosObtenidos: StateFlow<List<String>> = authRepository.currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else logrosRepository.getLogrosUsuario(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _filtroCategoria = MutableStateFlow<String?>(null)
    val filtroCategoria = _filtroCategoria.asStateFlow()

    private val _filtroDepartamento = MutableStateFlow("")
    val filtroDepartamento = _filtroDepartamento.asStateFlow()

    private val _filtroCiudad = MutableStateFlow("")
    val filtroCiudad = _filtroCiudad.asStateFlow()

    val mascotasFeed: StateFlow<List<Mascota>> = combine(
        mascotaRepository.mascotas,
        _filtroCategoria,
        _filtroDepartamento,
        _filtroCiudad,
        authRepository.currentUser
    ) { lista, categoria, depto, ciudad, user ->
        lista.filter { mascota ->
            val esPropia = user != null && mascota.autorId == user.id
            val esVisible = esPropia || 
                          mascota.estado == PublicacionEstado.VERIFICADA || 
                          mascota.estado == PublicacionEstado.RESUELTA ||
                          mascota.estado == PublicacionEstado.ADOPTADA
            
            val coincideCategoria = categoria == null || mascota.tipo.equals(categoria, ignoreCase = true)
            val coincideDepto = depto.isEmpty() || mascota.ubicacion.contains(depto, ignoreCase = true)
            val coincideCiudad = ciudad.isEmpty() || mascota.ubicacion.contains(ciudad, ignoreCase = true)
            
            esVisible && coincideCategoria && coincideDepto && coincideCiudad
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // MASCOTAS MAPA: Ahora sigue los mismos filtros que el Feed para ser consistente
    val mascotasMapa: StateFlow<List<Mascota>> = mascotasFeed

    val mascotasRecomendadas: StateFlow<List<Mascota>> = mascotaRepository.mascotas
        .map { lista -> lista.filter { it.esDestacada } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedNavItem = MutableStateFlow(0)
    val selectedNavItem = _selectedNavItem.asStateFlow()

    init {
        cargarUbicaciones()
    }

    private fun cargarUbicaciones() {
        viewModelScope.launch {
            try {
                allDepartments = apiColombia.getDepartamentos()
                allCities = apiColombia.getMunicipios()
                _listaDepartamentos.value = allDepartments.map { it.name ?: "" }.sorted()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun cargarCiudades(depto: String) {
        val deptoId = allDepartments.find { it.name == depto }?.id
        _listaCiudades.value = allCities.filter { it.departmentId == deptoId }.map { it.municipio ?: "" }.sorted()
    }

    fun setFiltroUbicacion(depto: String, ciudad: String) {
        _filtroDepartamento.value = depto
        _filtroCiudad.value = ciudad
    }

    fun onNavItemClicked(index: Int) {
        _selectedNavItem.value = index
    }

    fun filtrarPorCategoria(categoria: String?) {
        _filtroCategoria.value = categoria
    }

    fun asegurarMascotaEnRepo(mascota: Mascota) {
        if (mascotaRepository.getById(mascota.id) == null) {
            mascotaRepository.save(mascota)
        }
    }

    fun toggleLike(id: String) {
        val userId = currentUser.value?.id ?: return
        val mascota = mascotaRepository.getById(id)
        
        mascotaRepository.toggleLike(id, userId)
        
        if (mascota != null && mascota.autorId != userId && mascota.autorId.isNotEmpty()) {
            notificacionRepository.addNotificacion(
                tituloResId = R.string.home_notif_like_title,
                mensajeResId = R.string.home_notif_like_msg,
                mensajeArgs = listOf(mascota.nombre),
                tipo = "POST_VOTADO",
                userId = mascota.autorId
            )
        }
        
        viewModelScope.launch {
            val allMascotas = mascotaRepository.mascotas.value
            val misLikes = allMascotas.count { it.likerIds.contains(userId) }
            if (misLikes >= 10) {
                logrosRepository.ganarLogro(userId, "com_heart")
            }
        }
    }
}
