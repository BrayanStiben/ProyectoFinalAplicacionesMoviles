package com.example.seguimiento.features.FormularioDeAdopction

data class AdoptionFormState(
    // Paso 1
    val petType: String = "",
    val motivation: String = "",
    val expectations: String = "",
    val hoursAlone: Int = 0,

    // Paso 2 (De tu imagen)
    val petName: String = "",
    val petAge: String = "",
    val homeType: String = "",
    val hasFencedYard: Boolean = false,

    // Paso 3
    val awareOfCosts: Boolean = false,
    val contingencyPlan: String = "",
    val acceptsTerms: Boolean = false,
    val movingPlan: String = "",

    // Paso 4
    val idPhotoUri: String? = null,
    val referenceName: String = "",
    val referencePhone: String = ""
)