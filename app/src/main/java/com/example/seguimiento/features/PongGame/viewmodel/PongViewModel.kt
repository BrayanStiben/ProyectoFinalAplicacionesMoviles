package com.example.seguimiento.features.PongGame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PongUiState(
    val score: Int = 0,
    val isGameOver: Boolean = false,
    val pointsSaved: Boolean = false
)

@HiltViewModel
class PongViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PongUiState())
    val uiState: StateFlow<PongUiState> = _uiState.asStateFlow()

    fun updateScore(delta: Int) {
        _uiState.update { 
            val newScore = (it.score + delta).coerceAtLeast(0)
            it.copy(score = newScore) 
        }
    }

    fun onGameOver() {
        if (!_uiState.value.isGameOver) {
            _uiState.update { it.copy(isGameOver = true) }
            savePoints()
        }
    }

    private fun savePoints() {
        viewModelScope.launch {
            val user = authRepository.currentUser.firstOrNull()
            if (user != null && !_uiState.value.pointsSaved) {
                // Ahora el score es la cantidad de pongs directos (1, 2, 3...)
                // El usuario pidió quitar las unidades si es muy alto, 
                // pero si el score ya es 1, 2, 3, simplemente los sumamos.
                // Si quieres mantener la proporción de "quitar un 0", 
                // asumimos que el usuario gana 1 punto real por cada acierto.
                val finalPoints = _uiState.value.score
                if (finalPoints > 0) {
                    userRepository.addPoints(user.id, finalPoints)
                }
                _uiState.update { it.copy(pointsSaved = true) }
            }
        }
    }

    fun resetGame() {
        _uiState.update { PongUiState() }
    }
}
