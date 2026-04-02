package com.example.seguimiento.features.FormularioDeAdopction

import android.graphics.Bitmap

data class AdoptionFormState(
    // Datos de la mascota seleccionada
    val petId: String = "",
    val petType: String = "",
    val petName: String = "",
    val petAge: String = "",

    // Paso 1
    val motivation: String = "",
    val expectations: String = "",
    val hoursAlone: Int = 0,

    // Paso 2
    val homeType: String = "",
    val hasFencedYard: Boolean = false,

    // Paso 3
    val awareOfCosts: Boolean = false,
    val contingencyPlan: String = "",
    val acceptsTerms: Boolean = false,
    val movingPlan: String = "",

    // Paso 4
    val idPhotoUri: String? = null,
    val yardPhotoUri: String? = null,
    val referenceName: String = "",
    val referencePhone: String = "",
    val signatureBitmap: Bitmap? = null,
    val termsAccepted: Boolean = false
)