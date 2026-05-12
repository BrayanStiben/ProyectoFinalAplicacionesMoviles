package com.example.seguimiento.Dominio.repositorios

import android.net.Uri
import com.example.seguimiento.Dominio.modelos.CompraTienda
import com.example.seguimiento.Dominio.modelos.Producto
import kotlinx.coroutines.flow.StateFlow

interface TiendaRepository {
    val productos: StateFlow<List<Producto>>
    val compras: StateFlow<List<CompraTienda>>
    fun getAll(): List<Producto>
    fun getById(id: String): Producto?
    suspend fun save(producto: Producto, imageUri: Uri? = null)
    suspend fun update(producto: Producto)
    suspend fun delete(id: String)
    fun comprarProducto(producto: Producto, userId: String, userName: String, userEmail: String): Result<Unit>
}
