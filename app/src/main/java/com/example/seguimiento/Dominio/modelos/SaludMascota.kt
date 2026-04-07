package com.example.seguimiento.Dominio.modelos

data class Vacuna(
    val id: String,
    val nombre: String,
    val fecha: String,
    val proximaDosis: String
)

data class Desparasitacion(
    val id: String,
    val producto: String,
    val fecha: String
)

data class CitaVeterinaria(
    val id: String,
    val motivo: String,
    val fecha: String,
    val hora: String,
    val clinica: String
)

data class CarnetSalud(
    val mascotaId: String,
    val petId: String = "",
    val pin: String = "",
    val vacunas: List<Vacuna> = emptyList(),
    val desparasitaciones: List<Desparasitacion> = emptyList(),
    val citas: List<CitaVeterinaria> = emptyList()
)
