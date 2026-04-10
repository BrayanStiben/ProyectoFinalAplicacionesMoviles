package com.example.seguimiento.features.ConfirmacionMascotaFeliz

data class AdoptionUiState(
    val petName: String = "",
    val petType: String = "",
    val petAge: String = "",
    val petLoc: String = "",
    val petImg: String = "",
    val date: String = "",
    val shelter: String = "Refugio PetAdopt",
    val adoptedBy: String = "",
    // Campos para construcción dinámica de strings en UI
    val motivation: String = "",
    val homeType: String = "",
    val hoursAlone: String = "",
    val refName: String = "",
    val refPhone: String = "",
    val summary: String = "", // Mantener por compatibilidad si es necesario
    val adminComment: String = "" // Mantener por compatibilidad
)
