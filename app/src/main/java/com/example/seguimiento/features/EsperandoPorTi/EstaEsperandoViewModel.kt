package com.example.seguimiento.features.EsperandoPorTi

import androidx.lifecycle.ViewModel
import com.example.seguimiento.Dominio.repositorios.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EstaEsperandoViewModel @Inject constructor(
    private val mascotaRepository: MascotaRepository
) : ViewModel() {

    private val _mascotaDestacada = MutableStateFlow(Mascota("", "", "", ""))
    val mascotaDestacada: StateFlow<Mascota> = _mascotaDestacada.asStateFlow()

    fun seleccionarMascota(mascota: Mascota) {
        _mascotaDestacada.value = mascota
    }
}
