package com.example.seguimiento.Data.repositorios

import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import com.example.seguimiento.core.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : AuthRepository {

    private val _loggedUserId = MutableStateFlow<String?>(sessionManager.getSession())
    
    override val currentUser: StateFlow<User?> = _loggedUserId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else userRepository.users.map { users -> users.find { it.id == id } }
        }
        .stateIn(
            scope = CoroutineScope(Dispatchers.Main),
            started = SharingStarted.Eagerly,
            initialValue = sessionManager.getSession()?.let { id ->
                userRepository.users.value.find { it.id == id }
            }
        )
    
    private var currentVerificationCode: String? = null

    override suspend fun login(email: String, password: String): Result<User> {
        val user = userRepository.login(email, password)
        return if (user != null) {
            _loggedUserId.value = user.id
            sessionManager.saveSession(user.id)
            Result.success(user)
        } else {
            Result.failure(Exception("Credenciales inválidas"))
        }
    }

    override suspend fun register(user: User): Result<Unit> {
        userRepository.save(user)
        _loggedUserId.value = user.id
        sessionManager.saveSession(user.id)
        return Result.success(Unit)
    }

    override suspend fun logout() {
        _loggedUserId.value = null
        sessionManager.clearSession()
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
