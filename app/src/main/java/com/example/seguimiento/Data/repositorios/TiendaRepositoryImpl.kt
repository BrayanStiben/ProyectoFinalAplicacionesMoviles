package com.example.seguimiento.Data.repositorios

import android.net.Uri
import com.example.seguimiento.Dominio.modelos.CompraTienda
import com.example.seguimiento.Dominio.modelos.Producto
import com.example.seguimiento.Dominio.repositorios.TiendaRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.Dominio.repositorios.ImageStorageRepository
import com.example.seguimiento.core.utils.ResourceProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query as RetrofitQuery
import javax.inject.Inject
import javax.inject.Singleton

// APIs de Mascotas Reales
data class OffSearchResponse(val products: List<OffProduct>?)
data class OffProduct(val product_name: String?, val image_url: String?)

interface OpenPetFoodFactsApi {
    @GET("https://world.openpetfoodfacts.org/cgi/search.pl?json=1")
    suspend fun search(@RetrofitQuery("search_terms") terms: String, @RetrofitQuery("page_size") limit: Int = 50): OffSearchResponse
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
    private val firestore: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val notificacionRepository: NotificacionRepository,
    private val imageStorageRepository: ImageStorageRepository,
    private val resourceProvider: ResourceProvider
) : TiendaRepository {

    private val collection = firestore.collection("productos")
    private val comprasCollection = firestore.collection("compras_tienda")
    
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

    init {
        // Escucha productos
        collection.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val list = snapshot.toObjects(Producto::class.java)
                _productos.value = list
                if (list.isEmpty()) {
                    loadProductsFromApisAndSaveToFirebase()
                }
            }
        }

        // Escucha compras
        comprasCollection.orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    _compras.value = snapshot.toObjects(CompraTienda::class.java)
                }
            }
    }

    private fun loadProductsFromApisAndSaveToFirebase() {
        CoroutineScope(Dispatchers.IO).launch {
            val catFood = "Alimentos"
            try {
                val batch = firestore.batch()
                val response = petFoodApi.search("pet food", limit = 10)
                response.products?.take(10)?.forEach { item ->
                    if (!item.product_name.isNullOrEmpty() && !item.image_url.isNullOrEmpty()) {
                        val id = "pf_${item.product_name.hashCode()}"
                        val p = Producto(
                            id = id,
                            nombre = item.product_name.take(30),
                            descripcion = "Alimento premium",
                            precioPuntos = 500,
                            imagenUrl = item.image_url.replace("http://", "https://"),
                            stock = 20,
                            categoria = catFood
                        )
                        batch.set(collection.document(id), p)
                    }
                }
                batch.commit().await()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    override fun getAll(): List<Producto> = _productos.value
    override fun getById(id: String): Producto? = _productos.value.find { it.id == id }
    
    override suspend fun save(producto: Producto, imageUri: Uri?) {
        val id = if (producto.id.isEmpty()) collection.document().id else producto.id
        
        var finalUrl = producto.imagenUrl
        imageUri?.let { uri ->
            val imageUrl = imageStorageRepository.uploadImage(uri, "productos", "${id}_${producto.nombre}.jpg")
            if (imageUrl != null) {
                finalUrl = imageUrl
            }
        }

        collection.document(id).set(producto.copy(id = id, imagenUrl = finalUrl)).await()
    }

    override suspend fun update(producto: Producto) {
        collection.document(producto.id).set(producto).await()
    }

    override suspend fun delete(id: String) {
        collection.document(id).delete().await()
    }

    override fun comprarProducto(producto: Producto, userId: String, userName: String, userEmail: String): Result<Unit> {
        return try {
            if (producto.stock <= 0) return Result.failure(Exception("Producto agotado"))
            
            val compra = CompraTienda(
                productoId = producto.id,
                productoNombre = producto.nombre,
                userId = userId,
                userName = userName,
                userEmail = userEmail,
                puntosGastados = producto.precioPuntos
            )

            val batch = firestore.batch()
            batch.update(collection.document(producto.id), "stock", producto.stock - 1)
            batch.set(comprasCollection.document(compra.id), compra)
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    batch.commit().await()
                    userRepository.addPoints(userId, -producto.precioPuntos)
                    
                    notificacionRepository.addNotificacion(
                        tituloResId = com.example.seguimiento.R.string.notif_shop_purchase_title,
                        mensajeResId = com.example.seguimiento.R.string.notif_shop_purchase_msg,
                        mensajeArgs = listOf(producto.nombre),
                        tipo = "SUCCESS",
                        userId = userId
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
