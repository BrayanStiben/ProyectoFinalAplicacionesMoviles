package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.CompraTienda
import com.example.seguimiento.Dominio.modelos.Producto
import com.example.seguimiento.Dominio.repositorios.TiendaRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.core.utils.ResourceProvider
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
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Locale

// --- DATA CLASSES PARA APIS ---
data class DummyJsonResponse(val products: List<DjProduct>?)
data class DjProduct(val id: Int, val title: String, val description: String, val thumbnail: String?)

data class OffSearchResponse(val products: List<OffProduct>?)
data class OffProduct(val product_name: String?, val image_url: String?)

// --- INTERFACES DE API ---
interface DummyJsonApi {
    @GET("https://dummyjson.com/products/search")
    suspend fun search(@Query("q") query: String, @Query("limit") limit: Int = 20): DummyJsonResponse
}

interface OpenPetFoodFactsApi {
    @GET("https://world.openpetfoodfacts.org/cgi/search.pl?json=1")
    suspend fun search(@Query("search_terms") terms: String, @Query("page_size") limit: Int = 20): OffSearchResponse
}

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

    private val dummyApi: DummyJsonApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DummyJsonApi::class.java)
    }

    private val petFoodApi: OpenPetFoodFactsApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://world.openpetfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenPetFoodFactsApi::class.java)
    }

    init {
        loadProductsFromApis()
    }

    private fun loadProductsFromApis() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allFetched = mutableListOf<Producto>()

                val djQueries = mapOf(
                    resourceProvider.getString(com.example.seguimiento.R.string.store_cat_toys) to "pet",
                    resourceProvider.getString(com.example.seguimiento.R.string.store_cat_acc) to "furniture",
                    resourceProvider.getString(com.example.seguimiento.R.string.store_cat_health) to "care"
                )
                
                djQueries.forEach { (categoria, query) ->
                    try {
                        val response = dummyApi.search(query, limit = 10)
                        response.products?.forEach { item ->
                            if (!item.thumbnail.isNullOrEmpty()) {
                                allFetched.add(
                                    Producto(
                                        id = "dj_${item.id}",
                                        nombre = item.title.take(25),
                                        descripcion = item.description.take(100),
                                        precioPuntos = (300..2000).random(),
                                        imagenUrl = item.thumbnail!!,
                                        stock = (1..10).random(),
                                        categoria = categoria
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) { e.printStackTrace() }
                }

                try {
                    val petFoodQueries = listOf("dog food", "cat food", "pet treat")
                    val foodCategory = resourceProvider.getString(com.example.seguimiento.R.string.store_cat_food)
                    val defaultFoodName = resourceProvider.getString(com.example.seguimiento.R.string.store_default_food_name)
                    val defaultFoodDesc = resourceProvider.getString(com.example.seguimiento.R.string.store_default_food_desc)

                    petFoodQueries.forEach { query ->
                        try {
                            val responsePf = petFoodApi.search(query, limit = 10)
                            responsePf.products?.filter { !it.image_url.isNullOrEmpty() }?.forEachIndexed { index, item ->
                                allFetched.add(
                                    Producto(
                                        id = "pf_${query}_$index",
                                        nombre = (item.product_name ?: defaultFoodName).take(30),
                                        descripcion = defaultFoodDesc,
                                        precioPuntos = (200..1500).random(),
                                        imagenUrl = item.image_url!!.replace("http://", "https://"),
                                        stock = (1..15).random(),
                                        categoria = foodCategory
                                    )
                                )
                            }
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                } catch (e: Exception) { e.printStackTrace() }

                if (allFetched.isNotEmpty()) {
                    _productos.value = allFetched.distinctBy { it.nombre }.shuffled()
                }

            } catch (e: Exception) {
                // Silently fail or use minimal fallback if absolutely necessary
            }
        }
    }

    override fun getAll(): List<Producto> = _productos.value

    override fun getById(id: String): Producto? = _productos.value.find { it.id == id }

    override fun save(producto: Producto) {
        _productos.update { currentList ->
            val index = currentList.indexOfFirst { it.id == producto.id }
            if (index != -1) {
                currentList.toMutableList().apply { set(index, producto) }
            } else {
                currentList + producto
            }
        }
    }

    override fun update(producto: Producto) {
        _productos.update { list ->
            list.map { if (it.id == producto.id) producto else it }
        }
    }

    override fun delete(id: String) {
        _productos.update { it.filter { p -> p.id != id } }
    }

    override fun comprarProducto(producto: Producto, userId: String, userName: String, userEmail: String): Result<Unit> {
        if (producto.stock <= 0) return Result.failure(Exception("Producto agotado"))
        
        val nuevaCompra = CompraTienda(
            productoId = producto.id,
            productoNombre = producto.nombre,
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            puntosGastados = producto.precioPuntos
        )
        
        _compras.update { it + nuevaCompra }

        _productos.update { list ->
            list.map { 
                if (it.id == producto.id) {
                    val newStock = it.stock - 1
                    if (newStock == 0) {
                        notificacionRepository.addNotificacion(
                            tituloResId = com.example.seguimiento.R.string.store_notif_out_of_stock_title,
                            mensajeResId = com.example.seguimiento.R.string.store_notif_out_of_stock_msg,
                            mensajeArgs = listOf(it.nombre),
                            tipo = "WARNING"
                        )
                    }
                    it.copy(stock = newStock)
                } else it
            }
        }

        notificacionRepository.addNotificacion(
            tituloResId = com.example.seguimiento.R.string.store_notif_success_title,
            mensajeResId = com.example.seguimiento.R.string.store_notif_success_msg,
            mensajeArgs = listOf(producto.nombre, producto.precioPuntos.toString()),
            tipo = "SUCCESS",
            userId = userId
        )

        return Result.success(Unit)
    }
}
