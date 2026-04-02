package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.Mascota
import com.example.seguimiento.Dominio.modelos.PublicacionEstado
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
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
data class FoxResponse(val image: String)

interface MascotaPublicApi {
    @GET("https://dog.ceo/api/breeds/image/random/10")
    suspend fun getRandomDogs(): DogResponse

    @GET("https://api.thecatapi.com/v1/images/search?limit=10")
    suspend fun getRandomCats(): List<CatResponse>

    @GET("https://randomfox.ca/floof/")
    suspend fun getRandomFox(): FoxResponse

    @GET("https://randomuser.me/api/?results=10&inc=name")
    suspend fun getRandomNames(): UserResponse

    @GET("https://api-colombia.com/api/v1/City")
    suspend fun getColombiaCities(): List<ColombiaCityResponse>
}

@Singleton
class MascotaRepositoryImpl @Inject constructor() : MascotaRepository {

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
                // Obtenemos datos de APIs con manejo de errores individual para asegurar variedad
                val names = try { api.getRandomNames().results.map { it.name.first } } catch (e: Exception) { emptyList() }
                val cities = try { api.getColombiaCities().shuffled().take(10).map { it.name } } catch (e: Exception) { emptyList() }
                val dogImages = try { api.getRandomDogs().message } catch (e: Exception) { emptyList() }
                val catImages = try { api.getRandomCats().map { it.url } } catch (e: Exception) { emptyList() }

                val fetchedList = mutableListOf<Mascota>()
                val categorias = listOf("Perro", "Gato", "Pájaro", "Hámster", "Hurón", "Reptil", "Pez", "Caballo", "Otro", "Perro")

                for (i in 0 until 10) {
                    val cat = categorias[i]
                    val imgUrl = when (cat) {
                        "Perro" -> dogImages.getOrNull(i % if(dogImages.isEmpty()) 1 else dogImages.size) ?: "https://images.unsplash.com/photo-1552053831-71594a27632d?q=80&w=500"
                        "Gato" -> catImages.getOrNull(i % if(catImages.isEmpty()) 1 else catImages.size) ?: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=500"
                        "Pájaro" -> "https://images.unsplash.com/photo-1552728089-57bdde30fc3e?q=80&w=500"
                        "Hámster" -> "https://images.unsplash.com/photo-1548767791-5146842e658a?q=80&w=500"
                        "Hurón" -> "https://images.unsplash.com/photo-1612195583950-b8fd34c87093?q=80&w=500"
                        "Reptil" -> "https://images.unsplash.com/photo-1504450874802-0ba2bcd9b5ae?q=80&w=500"
                        "Pez" -> "https://images.unsplash.com/photo-1522069169874-c58ec4b76be5?q=80&w=500"
                        "Caballo" -> "https://images.unsplash.com/photo-1553284965-83fd3e82fa5a?q=80&w=500"
                        else -> try { api.getRandomFox().image } catch (e: Exception) { "https://images.unsplash.com/photo-1543466835-00a7907e9de1?q=80&w=500" }
                    }

                    fetchedList.add(
                        Mascota(
                            id = "api_pet_$i",
                            nombre = names.getOrNull(i) ?: getPetNameFallback(i),
                            edad = "${(1..10).random()} años",
                            tipo = cat,
                            raza = "Rescatado",
                            ubicacion = cities.getOrNull(i) ?: getCityFallback(i),
                            descripcion = "¡Hola! Soy un $cat rescatado buscando un hogar donde me den mucho amor. Soy muy juguetón y amigable. ¡Ven a conocerme!",
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
                // Si falla catastróficamente, se mantiene el placeholder con descripciones bonitas
            }
        }
    }

    private fun getPetNameFallback(i: Int) = listOf("Max", "Bella", "Charlie", "Luna", "Rocky", "Lucy", "Cooper", "Daisy", "Milo", "Sophie")[i % 10]
    private fun getCityFallback(i: Int) = listOf("Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena", "Cúcuta", "Soledad", "Ibagué", "Bucaramanga", "Villavicencio")[i % 10]

    private fun getInitialPlaceholder(): List<Mascota> {
        val names = listOf("Rex", "Luna", "Toby", "Coco", "Bella", "Max", "Kira", "Simba", "Lola", "Thor")
        val cities = listOf("Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena", "Cúcuta", "Soledad", "Ibagué", "Bucaramanga", "Villavicencio")
        val types = listOf("Perro", "Gato", "Pájaro", "Hámster", "Hurón", "Reptil", "Pez", "Caballo", "Otro", "Perro")
        
        return List(10) { i ->
            Mascota(
                id = "init_$i",
                nombre = names[i],
                edad = "${(1..10).random()} años",
                tipo = types[i],
                raza = "Rescatado",
                ubicacion = cities[i],
                descripcion = "Busco un hogar lleno de amor. Soy un ${types[i]} muy cariñoso y juguetón. ¡Dame una oportunidad para ser tu mejor amigo!",
                imagenUrl = when (types[i]) {
                    "Perro" -> "https://images.unsplash.com/photo-1552053831-71594a27632d"
                    "Gato" -> "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba"
                    "Pájaro" -> "https://images.unsplash.com/photo-1552728089-57bdde30fc3e"
                    "Hámster" -> "https://images.unsplash.com/photo-1548767791-5146842e658a"
                    "Hurón" -> "https://images.unsplash.com/photo-1612195583950-b8fd34c87093"
                    "Reptil" -> "https://images.unsplash.com/photo-1504450874802-0ba2bcd9b5ae"
                    "Pez" -> "https://images.unsplash.com/photo-1522069169874-c58ec4b76be5"
                    "Caballo" -> "https://images.unsplash.com/photo-1553284965-83fd3e82fa5a"
                    else -> "https://images.unsplash.com/photo-1543466835-00a7907e9de1"
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
        _mascotas.update { currentList ->
            val index = currentList.indexOfFirst { it.id == mascota.id }
            if (index != -1) {
                currentList.toMutableList().apply { set(index, mascota) }
            } else {
                currentList + mascota
            }
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
        _mascotas.update { currentList ->
            currentList.map {
                if (it.id == id) it.copy(estado = estado, motivoRechazo = motivo) else it
            }
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
