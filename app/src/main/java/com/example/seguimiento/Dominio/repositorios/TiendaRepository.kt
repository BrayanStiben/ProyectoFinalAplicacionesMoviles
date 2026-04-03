package com.example.seguimiento.Dominio.repositorios

import com.example.seguimiento.Dominio.modelos.CompraTienda
import com.example.seguimiento.Dominio.modelos.Producto
import kotlinx.coroutines.flow.StateFlow

interface TiendaRepository {
    val productos: StateFlow<List<Producto>>
    val compras: StateFlow<List<CompraTienda>>
    fun getAll(): List<Producto>
    fun getById(id: String): Producto?
    fun save(producto: Producto)
    fun update(producto: Producto)
    fun delete(id: String)
    fun comprarProducto(producto: Producto, userId: String, userName: String, userEmail: String): Result<Unit>
}
