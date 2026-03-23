package com.example.seguimiento.features.nutricionanimal

import androidx.lifecycle.ViewModel
import com.example.seguimiento.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NutricionViewModel : ViewModel() {

    private val _regulaciones = MutableStateFlow(listOf(
        Regulacion(
            id = 1,
            titulo = "Ley General de Protección Animal",
            subtitulo = "Obtener más información",
            imagenRecurso = R.drawable.proteccion,
            url = "https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=68135"
        ),
        Regulacion(
            id = 2,
            titulo = "Normativa Federal",
            subtitulo = "Residuos de zoopolítica animales (NOM)",
            imagenRecurso = R.drawable.leyes,
            url = "https://www.participacionbogota.gov.co/atencion-al-ciudadano/cuales-son-las-leyes-que-protegen-los-animales"
        ),
        Regulacion(
            id = 3,
            titulo = "Normas y regulaciones",
            subtitulo = "Conoce tus derechos y deberes",
            imagenRecurso = R.drawable.normasyregulaciones,
            url = "https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=260800"
        )
    ))

    val regulaciones: StateFlow<List<Regulacion>> = _regulaciones.asStateFlow()

    private val _itemSeleccionado = MutableStateFlow(0)
    val itemSeleccionado = _itemSeleccionado.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow("nutricion")
    val categoriaSeleccionada = _categoriaSeleccionada.asStateFlow()

    fun cambiarNavegacion(i: Int) {
        _itemSeleccionado.value = i
    }

    fun seleccionarCategoria(categoria: String) {
        _categoriaSeleccionada.value = categoria
    }
}