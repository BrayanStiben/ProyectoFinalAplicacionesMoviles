package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.CompraTienda
import com.example.seguimiento.Dominio.modelos.Producto
import com.example.seguimiento.Dominio.repositorios.TiendaRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.core.utils.ResourceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

// APIs de Mascotas Reales
data class OffSearchResponse(val products: List<OffProduct>?)
data class OffProduct(val product_name: String?, val image_url: String?)

interface OpenPetFoodFactsApi {
    @GET("https://world.openpetfoodfacts.org/cgi/search.pl?json=1")
    suspend fun search(@Query("search_terms") terms: String, @Query("page_size") limit: Int = 50): OffSearchResponse
}

interface DogImageApi {
    @GET("https://dog.ceo/api/breeds/image/random/20")
    suspend fun getRandomImages(): DogImageResponse
}
data class DogImageResponse(val message: List<String>)

interface CatImageApi {
    @GET("https://api.thecatapi.com/v1/images/search?limit=20")
    suspend fun getRandomImages(): List<CatImageResponse>
}
data class CatImageResponse(val url: String)

@Singleton
class TiendaRepositoryImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val notificacionRepository: NotificacionRepository,
    private val resourceProvider: ResourceProvider
) : TiendaRepository {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    override val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _compras = MutableStateFlow<List<CompraTienda>>(emptyList())
    override val compras: StateFlow<List<CompraTienda>> = _compras.asStateFlow()

    private val petFoodApi: OpenPetFoodFactsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://world.openpetfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenPetFoodFactsApi::class.java)
    }

    private val dogApi: DogImageApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DogImageApi::class.java)
    }

    private val catApi: CatImageApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatImageApi::class.java)
    }

    init {
        loadProductsFromApis()
    }

    private fun loadProductsFromApis() {
        CoroutineScope(Dispatchers.IO).launch {
            val catToys = resourceProvider.getString(com.example.seguimiento.R.string.store_cat_toys)
            val catAcc = resourceProvider.getString(com.example.seguimiento.R.string.store_cat_acc)
            val catHealth = resourceProvider.getString(com.example.seguimiento.R.string.store_cat_health)
            val catFood = resourceProvider.getString(com.example.seguimiento.R.string.store_cat_food)

            // 1. Cargar Comida Real (OpenPetFoodFacts)
            launch {
                try {
                    val queries = listOf("dog food", "cat food", "pet snacks")
                    queries.forEach { query ->
                        val response = petFoodApi.search(query, limit = 15)
                        val items = response.products?.mapNotNull { item ->
                            if (item.image_url.isNullOrEmpty() || item.product_name.isNullOrEmpty()) return@mapNotNull null
                            Producto(
                                id = "pf_${item.product_name.hashCode()}",
                                nombre = item.product_name.take(35),
                                descripcion = "Alimento premium certificado para mascotas.",
                                precioPuntos = (400..1200).random(),
                                imagenUrl = item.image_url.replace("http://", "https://"),
                                stock = (10..30).random(),
                                categoria = catFood
                            )
                        } ?: emptyList()
                        _productos.update { (it + items).distinctBy { p -> p.id } }
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }

            // 2. Cargar Juguetes y Accesorios usando imágenes reales de Perros/Gatos
            launch {
                try {
                    val dogImages = dogApi.getRandomImages().message
                    val catImages = catApi.getRandomImages().map { it.url }
                    val allPetImages = (dogImages + catImages).shuffled()

                    val items = allPetImages.mapIndexed { index, url ->
                        val (nombre, cat, desc) = when (index % 3) {
                            0 -> Triple("Juguete Interactivo", catToys, "Pelota resistente para horas de diversión.")
                            1 -> Triple("Arnés Ergonómico", catAcc, "Máximo confort y seguridad para paseos.")
                            else -> Triple("Kit de Higiene Pro", catHealth, "Limpieza profunda y cuidado de la piel.")
                        }

                        Producto(
                            id = "pet_item_$index",
                            nombre = "$nombre ${index + 1}",
                            descripcion = desc,
                            precioPuntos = (250..1500).random(),
                            imagenUrl = url,
                            stock = (5..15).random(),
                            categoria = cat
                        )
                    }
                    _productos.update { (it + items).distinctBy { p -> p.id } }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    override fun getAll(): List<Producto> = _productos.value
    override fun getById(id: String): Producto? = _productos.value.find { it.id == id }
    override fun save(producto: Producto) { _productos.update { it + producto } }
    override fun update(producto: Producto) { _productos.update { list -> list.map { if (it.id == producto.id) producto else it } } }
    override fun delete(id: String) { _productos.update { it.filter { p -> p.id != id } } }

    override fun comprarProducto(producto: Producto, userId: String, userName: String, userEmail: String): Result<Unit> {
        if (producto.stock <= 0) return Result.failure(Exception("Producto agotado"))
        _compras.update { it + CompraTienda(productoId = producto.id, productoNombre = producto.nombre, userId = userId, userName = userName, userEmail = userEmail, puntosGastados = producto.precioPuntos) }
        _productos.update { list -> list.map { if (it.id == producto.id) it.copy(stock = it.stock - 1) else it } }
        return Result.success(Unit)
    }
}
