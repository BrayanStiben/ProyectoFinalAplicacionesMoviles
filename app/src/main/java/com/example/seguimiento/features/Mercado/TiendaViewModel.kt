package com.example.seguimiento.features.Mercado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Producto
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.Dominio.repositorios.TiendaRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import com.example.seguimiento.core.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.seguimiento.R

data class TiendaUiState(
    val productos: List<Producto> = emptyList(),
    val categoriaSeleccionada: String = "Todos",
    val puntosUsuario: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TiendaViewModel @Inject constructor(
    private val tiendaRepository: TiendaRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val notificacionRepository: NotificacionRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _categoriaSeleccionada = MutableStateFlow("Todos")
    
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val uiState: StateFlow<TiendaUiState> = combine(
        tiendaRepository.productos,
        _categoriaSeleccionada,
        authRepository.currentUser
    ) { productos, categoria, user ->
        // Obtenemos los nombres localizados de las categorías para comparar correctamente
        val catFood = resourceProvider.getString(R.string.store_cat_food)
        val catToys = resourceProvider.getString(R.string.store_cat_toys)
        val catAcc = resourceProvider.getString(R.string.store_cat_acc)
        val catHealth = resourceProvider.getString(R.string.store_cat_health)

        val categoriaReal = when(categoria) {
            "Comida", "Food" -> catFood
            "Juguetes", "Toys" -> catToys
            "Accesorios", "Accessories" -> catAcc
            "Salud", "Health" -> catHealth
            else -> "Todos"
        }

        val filtrados = if (categoriaReal == "Todos" || categoria == "All" || categoria == "Todos") {
            productos
        } else {
            productos.filter { it.categoria == categoriaReal }
        }

        TiendaUiState(
            productos = filtrados,
            categoriaSeleccionada = if (categoria == "All") "Todos" else categoria,
            puntosUsuario = user?.points ?: 0,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TiendaUiState(isLoading = true)
    )

    fun seleccionarCategoria(categoria: String) {
        _categoriaSeleccionada.value = categoria
    }

    fun comprarProducto(producto: Producto) {
        viewModelScope.launch {
            val user = authRepository.currentUser.value ?: return@launch
            
            if (user.points < producto.precioPuntos) {
                notificacionRepository.addNotificacion(
                    tituloResId = R.string.store_notif_insufficient_points_title,
                    mensajeResId = R.string.store_notif_insufficient_points_msg,
                    mensajeArgs = listOf(producto.precioPuntos.toString(), producto.nombre),
                    userId = user.id
                )
                return@launch
            }

            val result = tiendaRepository.comprarProducto(
                producto = producto,
                userId = user.id,
                userName = user.name,
                userEmail = user.email
            )

            if (result.isSuccess) {
                userRepository.addPoints(user.id, -producto.precioPuntos)
                
                notificacionRepository.addNotificacion(
                    tituloResId = R.string.store_notif_redeem_success_title,
                    mensajeResId = R.string.store_notif_redeem_success_msg,
                    mensajeArgs = listOf(producto.nombre),
                    userId = user.id
                )
            }
        }
    }
}
