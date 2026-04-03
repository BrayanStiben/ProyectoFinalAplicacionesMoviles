package com.example.seguimiento.features.MercadoAdmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.modelos.Producto
import com.example.seguimiento.Dominio.repositorios.TiendaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TiendaAdminViewModel @Inject constructor(
    private val tiendaRepository: TiendaRepository
) : ViewModel() {

    val productos = tiendaRepository.productos

    fun subirProducto(nombre: String, desc: String, puntos: Int, url: String, stock: Int, categoria: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val nuevo = Producto(
                nombre = nombre,
                descripcion = desc,
                precioPuntos = puntos,
                imagenUrl = url,
                stock = stock,
                categoria = categoria
            )
            tiendaRepository.save(nuevo)
            onSuccess()
        }
    }

    fun actualizarProducto(producto: Producto, onSuccess: () -> Unit) {
        viewModelScope.launch {
            tiendaRepository.update(producto)
            onSuccess()
        }
    }

    fun eliminarProducto(id: String) {
        viewModelScope.launch {
            tiendaRepository.delete(id)
        }
    }
}
