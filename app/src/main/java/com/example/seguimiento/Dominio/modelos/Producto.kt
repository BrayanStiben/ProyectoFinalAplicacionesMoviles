package com.example.seguimiento.Dominio.modelos

data class Producto(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precioPuntos: Int = 0,
    val imagenUrl: String = "",
    val stock: Int = 0,
    val categoria: String = "" // "Alimentos", "Juguetes", "Accesorios", "Salud"
)
