package com.example.seguimiento.features.asifunciona

import androidx.lifecycle.ViewModel
import com.example.seguimiento.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ProcesoBio(val id: Int, val titulo: String, val descripcion: String, val icon: Int)

@HiltViewModel
class AsiFuncionaViewModel @Inject constructor() : ViewModel() {

    private val _procesos = MutableStateFlow(listOf(
        ProcesoBio(1, "Ingestión", "El alimento entra por la boca y comienza la descomposición mecánica.", R.drawable.img1),
        ProcesoBio(2, "Digestión Gástrica", "Los ácidos estomacales descomponen las proteínas de forma eficiente.", R.drawable.img2),
        ProcesoBio(3, "Absorción", "Los nutrientes pasan al torrente sanguíneo a través del intestino delgado.", R.drawable.img3),
        ProcesoBio(4, "Excreción", "Eliminación de desechos no digeribles para mantener el sistema limpio.", R.drawable.img4)
    ))
    val procesos: StateFlow<List<ProcesoBio>> = _procesos.asStateFlow()
}
