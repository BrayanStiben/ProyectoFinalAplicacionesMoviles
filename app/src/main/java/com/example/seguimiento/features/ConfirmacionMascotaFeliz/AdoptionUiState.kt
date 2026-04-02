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
    val summary: String = "",
    val adminComment: String = ""
)
