package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userRepository: UserRepository
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private var currentVerificationCode: String? = null

    override suspend fun login(email: String, password: String): Result<User> {
        val user = userRepository.login(email, password)
        return if (user != null) {
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Credenciales inválidas"))
        }
    }

    override suspend fun register(user: User): Result<Unit> {
        userRepository.save(user)
        return Result.success(Unit)
    }

    override suspend fun logout() {
        _currentUser.value = null
    }

    override suspend fun recoverPassword(email: String): Result<Unit> {
        return Result.success(Unit)
    }

    override fun generateVerificationCode(): String {
        val code = Random.nextInt(1000, 9999).toString()
        currentVerificationCode = code
        return code
    }

    override fun getVerificationCode(): String? {
        return currentVerificationCode
    }
}
