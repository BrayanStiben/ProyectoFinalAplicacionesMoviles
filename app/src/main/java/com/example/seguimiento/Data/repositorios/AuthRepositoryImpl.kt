package com.example.seguimiento.Data.repositorios

import android.net.Uri
import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.Dominio.repositorios.AuthRepository
import com.example.seguimiento.Dominio.repositorios.UserRepository
import com.example.seguimiento.core.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : AuthRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUser: StateFlow<User?> = callbackFlow<String?> {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }.flatMapLatest { uid ->
        if (uid == null) flowOf(null)
        else {
            // Intentamos obtener el usuario del flujo de la base de datos
            userRepository.users.map { users -> 
                users.find { it.id == uid }
            }
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Main),
        started = SharingStarted.Eagerly,
        initialValue = null
    )
    
    private var currentVerificationCode: String? = null

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            // 1. Autenticación con Firebase Auth
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("No se pudo obtener el usuario de Auth")
            
            // 2. Obtener el perfil de la base de datos (Usamos el findById suspendido que busca en servidor)
            val user = userRepository.findById(firebaseUser.uid) 
                ?: throw Exception("Perfil de usuario no encontrado en la base de datos")
            
            sessionManager.saveSession(user.id)
            Result.success(user)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error en login: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun register(user: User, password: String, imageUri: Uri?): Result<Unit> {
        return try {
            // 1. Crear usuario en Auth
            val result = firebaseAuth.createUserWithEmailAndPassword(user.email, password).await()
            val firebaseUser = result.user ?: throw Exception("Error al crear la cuenta de autenticación")
            
            // 2. Guardar perfil en Firestore con el UID correcto
            val newUser = user.copy(id = firebaseUser.uid)
            userRepository.save(newUser, imageUri)
            
            // 3. Guardar sesión local
            sessionManager.saveSession(newUser.id)
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error en registro: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(user: User, imageUri: Uri?): Result<Unit> {
        return try {
            userRepository.save(user, imageUri)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        sessionManager.clearSession()
    }

    override suspend fun recoverPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
