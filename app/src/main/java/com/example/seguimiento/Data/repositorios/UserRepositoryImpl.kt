package com.example.seguimiento.Data.repositorios

import android.net.Uri
import com.example.seguimiento.Dominio.modelos.User
import com.example.seguimiento.Dominio.modelos.UserRole
import com.example.seguimiento.Dominio.modelos.UsuarioEstadisticas
import com.example.seguimiento.Dominio.repositorios.UserRepository
import com.example.seguimiento.Dominio.repositorios.ImageStorageRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val imageStorageRepository: ImageStorageRepository
) : UserRepository {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    override val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        // Escucha en tiempo real
        firestore.collection("usuarios")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val userList = snapshot.toObjects(User::class.java)
                    _users.value = userList
                    
                    // Si la colección está vacía en Firebase, subimos los datos iniciales
                    if (userList.isEmpty()) {
                        seedInitialData()
                    }
                }
            }
    }

    private fun seedInitialData() {
        val initialUsers = listOf(
            User(id = "admin_id", name = "Admin Sistema", email = "admin@gmail.com", role = UserRole.ADMIN, points = 5000),
            User(id = "user_test", name = "Usuario Prueba", email = "test@gmail.com", role = UserRole.USER, points = 100)
        )
        val batch = firestore.batch()
        initialUsers.forEach { user ->
            val docRef = firestore.collection("usuarios").document(user.id)
            batch.set(docRef, user)
        }
        batch.commit().addOnFailureListener { it.printStackTrace() }
    }

    override suspend fun save(user: User, imageUri: Uri?) {
        val id = if (user.id.isEmpty()) firestore.collection("usuarios").document().id else user.id
        
        var finalUrl = user.profilePictureUrl
        android.util.Log.d("UserRepository", "Iniciando save para usuario $id. Uri recibida: $imageUri")
        
        imageUri?.let { uri ->
            android.util.Log.d("UserRepository", "Subiendo imagen a ImgBB...")
            val imageUrl = imageStorageRepository.uploadImage(uri, "usuarios", "${id}_profile.jpg")
            if (imageUrl != null) {
                finalUrl = imageUrl
                android.util.Log.d("UserRepository", "URL de imagen obtenida: $finalUrl")
            } else {
                android.util.Log.e("UserRepository", "Error: No se pudo subir la imagen a ImgBB.")
            }
        } ?: run {
            android.util.Log.w("UserRepository", "No se recibió Uri de imagen para subir.")
        }

        val userToSave = user.copy(id = id, profilePictureUrl = finalUrl)
        android.util.Log.d("UserRepository", "Guardando en Firestore: $userToSave")
        
        firestore.collection("usuarios").document(id).set(userToSave).await()
        android.util.Log.d("UserRepository", "Guardado exitoso en Firestore.")
    }

    override fun findById(id: String): User? = _users.value.find { it.id == id }

    override fun findByEmail(email: String): User? = _users.value.find { it.email == email }

    override fun getUsuariosConEstadisticas(): List<UsuarioEstadisticas> {
        return _users.value.map { UsuarioEstadisticas(it.name, it.departamento, it.city) }
    }

    override suspend fun deleteAccount(id: String) {
        firestore.collection("usuarios").document(id).delete().await()
    }

    override suspend fun incrementRejectionCount(userId: String) {
        val user = findById(userId) ?: return
        firestore.collection("usuarios").document(userId).update("rejectionCount", user.rejectionCount + 1).await()
    }

    override suspend fun resetRejectionCount(userId: String) {
        val user = findById(userId) ?: return
        firestore.collection("usuarios").document(userId).update("rejectionCount", 0).await()
    }

    override suspend fun applyPenalty(userId: String, durationMillis: Long) {
        val endTime = System.currentTimeMillis() + durationMillis
        firestore.collection("usuarios").document(userId).update("penaltyEndTime", endTime).await()
    }

    override suspend fun addPoints(userId: String, points: Int) {
        val user = findById(userId) ?: return
        firestore.collection("usuarios").document(userId).update("points", user.points + points).await()
    }

    override suspend fun addBadge(userId: String, badgeId: String) {
        val user = findById(userId) ?: return
        if (!user.badges.contains(badgeId)) {
            val newList = user.badges + badgeId
            firestore.collection("usuarios").document(userId).update("badges", newList).await()
        }
    }

    override suspend fun updateFcmToken(userId: String, token: String) {
        firestore.collection("usuarios").document(userId).update("fcmToken", token).await()
    }
}
