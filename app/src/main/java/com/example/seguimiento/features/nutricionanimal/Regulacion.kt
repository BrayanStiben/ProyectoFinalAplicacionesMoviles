package com.example.seguimiento.features.nutricionanimal

import androidx.annotation.DrawableRes

data class Regulacion(
    val id: Int,
    val titulo: String,
    val subtitulo: String,
    @DrawableRes val imagenRecurso: Int,
    val url: String
)