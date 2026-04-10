package com.example.seguimiento.features.nutricionanimal

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Regulacion(
    val id: Int,
    @StringRes val tituloRes: Int,
    @StringRes val subtituloRes: Int,
    @DrawableRes val imagenRecurso: Int,
    val url: String
)