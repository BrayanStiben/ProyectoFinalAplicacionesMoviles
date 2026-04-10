package com.example.seguimiento.features.QueNesecitoParaAdoptar

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

import com.example.seguimiento.R

enum class SeccionAdopcion { TUS_DATOS, CALCULAR_COMIDA, HOGAR_FELIZ }

@HiltViewModel
class QueNesecitoParaAdoptarViewModel @Inject constructor() : ViewModel() {

    private val _seccionSeleccionada = MutableStateFlow<SeccionAdopcion?>(null)
    val seccionSeleccionada = _seccionSeleccionada.asStateFlow()

    private val _tabSeleccionada = MutableStateFlow(0)
    val tabSeleccionada = _tabSeleccionada.asStateFlow()

    val contenidosPopUp = mapOf(
        SeccionAdopcion.TUS_DATOS to R.string.req_popup_content_data,
        SeccionAdopcion.CALCULAR_COMIDA to R.string.req_popup_content_food,
        SeccionAdopcion.HOGAR_FELIZ to R.string.req_popup_content_home
    )

    val titulosPopUp = mapOf(
        SeccionAdopcion.TUS_DATOS to R.string.req_popup_title_data,
        SeccionAdopcion.CALCULAR_COMIDA to R.string.req_popup_title_food,
        SeccionAdopcion.HOGAR_FELIZ to R.string.req_popup_title_home
    )

    fun abrirPopUp(seccion: SeccionAdopcion) {
        _seccionSeleccionada.value = seccion
    }

    fun cerrarPopUp() {
        _seccionSeleccionada.value = null
    }

    fun cambiarTab(indice: Int) {
        _tabSeleccionada.value = indice
    }
}
