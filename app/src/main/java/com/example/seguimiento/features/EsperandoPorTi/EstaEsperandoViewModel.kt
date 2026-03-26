package com.example.seguimiento.features.EsperandoPorTi

import androidx.lifecycle.ViewModel
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EstaEsperandoViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository
) : ViewModel() {
    // Lógica para gestionar la mascota actual
}
