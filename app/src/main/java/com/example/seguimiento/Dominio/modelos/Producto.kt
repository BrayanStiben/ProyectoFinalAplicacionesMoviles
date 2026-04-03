package com.example.seguimiento.Dominio.modelos

import java.util.UUID

data class Producto(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val descripcion: String,
    val precioPuntos: Int,
    val imagenUrl: String,
    val stock: Int,
    val categoria: String // "Comida", "Juguetes", "Accesorios", "Salud"
)
