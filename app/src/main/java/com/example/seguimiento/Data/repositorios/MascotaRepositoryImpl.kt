package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

// --- DATA CLASSES PARA RESPUESTAS ---
data class DogResponse(val message: List<String>)
data class CatResponse(val url: String)
data class UserResponse(val results: List<UserResult>)
data class UserResult(val name: UserName)
data class UserName(val first: String)
data class ColombiaCityResponse(val name: String)

interface MascotaPublicApi {
    @GET("https://dog.ceo/api/breeds/image/random/10")
    suspend fun getRandomDogs(): DogResponse

    @GET("https://api.thecatapi.com/v1/images/search?limit=10")
    suspend fun getRandomCats(): List<CatResponse>

    @GET("https://randomuser.me/api/?results=10&inc=name")
    suspend fun getRandomNames(): UserResponse

    @GET("https://api-colombia.com/api/v1/City")
    suspend fun getColombiaCities(): List<ColombiaCityResponse>
}

@Singleton
class MascotaRepositoryImpl @Inject constructor(
    private val notificacionRepository: NotificacionRepository
) : MascotaRepository {

    private val _mascotas = MutableStateFlow<List<Mascota>>(getInitialPlaceholder())
    override val mascotas: StateFlow<List<Mascota>> = _mascotas.asStateFlow()

    private val api: MascotaPublicApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api-colombia.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MascotaPublicApi::class.java)
    }

    init {
        loadData()
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val names = try { api.getRandomNames().results.map { it.name.first } } catch (e: Exception) { emptyList() }
                val cities = try { api.getColombiaCities().shuffled().take(10).map { it.name } } catch (e: Exception) { emptyList() }
                val dogImages = try { api.getRandomDogs().message } catch (e: Exception) { emptyList() }
                val catImages = try { api.getRandomCats().map { it.url } } catch (e: Exception) { emptyList() }

                val fetchedList = mutableListOf<Mascota>()
                
                for (i in 0 until 10) {
                    val isDog = i % 2 == 0
                    val type = if (isDog) "Perro" else "Gato"
                    
                    val imgUrl = if (isDog) {
                        dogImages.getOrNull(i / 2 % if(dogImages.isEmpty()) 1 else dogImages.size) ?: "https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=500"
                    } else {
                        catImages.getOrNull(i / 2 % if(catImages.isEmpty()) 1 else catImages.size) ?: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=500"
                    }

                    fetchedList.add(
                        Mascota(
                            id = "api_pet_$i",
                            nombre = names.getOrNull(i) ?: getPetNameFallback(i),
                            edad = "${(1..10).random()} años",
                            tipo = type,
                            raza = if(isDog) "Rescatado (Canino)" else "Rescatado (Felino)",
                            ubicacion = cities.getOrNull(i) ?: getCityFallback(i),
                            descripcion = "¡Hola! Soy un $type rescatado buscando un hogar lleno de amor. Soy muy juguetón y amigable. ¡Ven a conocerme!",
                            imagenUrl = imgUrl,
                            esDestacada = i < 3,
                            estado = PublicacionEstado.VERIFICADA,
                            lat = 4.6097, lng = -74.0817, autorId = "admin"
                        )
                    )
                }
                
                if (fetchedList.isNotEmpty()) {
                    _mascotas.value = fetchedList
                }
            } catch (e: Exception) {
                // Se mantiene el placeholder en caso de error
            }
        }
    }

    private fun getPetNameFallback(i: Int) = listOf("Max", "Bella", "Charlie", "Luna", "Rocky", "Lucy", "Cooper", "Daisy", "Milo", "Sophie")[i % 10]
    private fun getCityFallback(i: Int) = listOf("Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena", "Cúcuta", "Soledad", "Ibagué", "Bucaramanga", "Villavicencio")[i % 10]

    private fun getInitialPlaceholder(): List<Mascota> {
        val names = listOf("Rex", "Luna", "Toby", "Coco", "Bella", "Max", "Kira", "Simba", "Lola", "Thor")
        val cities = listOf("Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena", "Cúcuta", "Soledad", "Ibagué", "Bucaramanga", "Villavicencio")
        
        return List(10) { i ->
            val isDog = i % 2 == 0
            val type = if (isDog) "Perro" else "Gato"
            Mascota(
                id = "init_$i",
                nombre = names[i],
                edad = "${(1..10).random()} años",
                tipo = type,
                raza = if(isDog) "Pastor Alemán Mix" else "Común Europeo",
                ubicacion = cities[i],
                descripcion = "Busco un hogar lleno de amor. Soy un $type muy cariñoso y juguetón. ¡Dame una oportunidad para ser tu mejor amigo!",
                imagenUrl = if (isDog) {
                    "https://images.unsplash.com/photo-1552053831-71594a27632d"
                } else {
                    "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba"
                },
                esDestacada = i < 3,
                estado = PublicacionEstado.VERIFICADA,
                lat = 4.6097, lng = -74.0817, autorId = "admin"
            )
        }
    }

    override fun getAll(): List<Mascota> = _mascotas.value

    override fun getById(id: String): Mascota? = _mascotas.value.find { it.id == id }

    override fun save(mascota: Mascota) {
        val isNew = _mascotas.value.none { it.id == mascota.id }
        _mascotas.update { currentList ->
            val index = currentList.indexOfFirst { it.id == mascota.id }
            if (index != -1) {
                currentList.toMutableList().apply { set(index, mascota) }
            } else {
                currentList + mascota
            }
        }
        
        if (isNew) {
            notificacionRepository.addNotificacion(
                titulo = "¡Nueva Mascota!",
                mensaje = "Se ha publicado a ${mascota.nombre} para adopción.",
                tipo = "SUCCESS"
            )
        }
    }

    override fun delete(id: String) {
        _mascotas.update { it.filter { m -> m.id != id } }
    }

    override fun getDestacadas(): List<Mascota> = _mascotas.value.filter { it.esDestacada }

    override fun getVerificadas(categoria: String?, lat: Double?, lng: Double?, radioKm: Double?): List<Mascota> {
        return _mascotas.value.filter { mascota ->
            val matchesEstado = mascota.estado == PublicacionEstado.VERIFICADA || mascota.estado == PublicacionEstado.RESUELTA || mascota.estado == PublicacionEstado.ADOPTADA
            val matchesCategoria = categoria == null || mascota.tipo.equals(categoria, ignoreCase = true)
            
            val matchesUbicacion = if (lat != null && lng != null && radioKm != null) {
                calcularDistancia(lat, lng, mascota.lat, mascota.lng) <= radioKm
            } else {
                true
            }
            
            matchesEstado && matchesCategoria && matchesUbicacion
        }
    }

    override fun getPendientesModeracion(): List<Mascota> {
        return _mascotas.value.filter { it.estado == PublicacionEstado.PENDIENTE }
    }

    override fun actualizarEstado(id: String, estado: PublicacionEstado, motivo: String) {
        val mascota = getById(id)
        _mascotas.update { currentList ->
            currentList.map {
                if (it.id == id) it.copy(estado = estado, motivoRechazo = motivo) else it
            }
        }
        
        mascota?.let {
            val msj = if(estado == PublicacionEstado.VERIFICADA) "ha sido aprobada." else "ha sido rechazada."
            notificacionRepository.addNotificacion(
                titulo = "Estado de publicación",
                mensaje = "Tu mascota ${it.nombre} $msj",
                tipo = if(estado == PublicacionEstado.VERIFICADA) "SUCCESS" else "ERROR",
                userId = it.autorId
            )
        }
    }

    override fun toggleLike(mascotaId: String, userId: String) {
        _mascotas.update { currentList ->
            currentList.map { mascota ->
                if (mascota.id == mascotaId) {
                    val currentLikers = mascota.likerIds.toMutableList()
                    if (currentLikers.contains(userId)) {
                        currentLikers.remove(userId)
                    } else {
                        currentLikers.add(userId)
                    }
                    mascota.copy(likerIds = currentLikers)
                } else mascota
            }
        }
    }

    private fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371 
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
