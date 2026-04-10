package com.example.seguimiento.features.Mercado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Producto
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.NotificacionRepository
import com.example.seguimiento.Dominio.repositorios.TiendaRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
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
    private val notificacionRepository: NotificacionRepository
) : ViewModel() {

    private val _categoriaSeleccionada = MutableStateFlow("Todos")
    
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val uiState: StateFlow<TiendaUiState> = combine(
        tiendaRepository.productos,
        _categoriaSeleccionada,
        authRepository.currentUser
    ) { productos, categoria, user ->
        val filtrados = if (categoria == "All" || categoria == "Todos") productos else productos.filter { it.categoria == categoria }
        TiendaUiState(
            productos = filtrados,
            categoriaSeleccionada = if (categoria == "All") "Todos" else categoria,
            puntosUsuario = user?.points ?: 0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TiendaUiState(isLoading = true))

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
                // Restar puntos al usuario
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
