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
        firestore.collection("usuarios")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val userList = snapshot.toObjects(User::class.java)
                    _users.value = userList
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
        
        imageUri?.let { uri ->
            val imageUrl = imageStorageRepository.uploadImage(uri, "usuarios", "${id}_profile.jpg")
            if (imageUrl != null) finalUrl = imageUrl
        }

        val userToSave = user.copy(id = id, profilePictureUrl = finalUrl)
        firestore.collection("usuarios").document(id).set(userToSave).await()
    }

    // MODIFICADO: Ahora busca en el servidor si no está en la memoria local
    override suspend fun findById(id: String): User? {
        val local = _users.value.find { it.id == id }
        if (local != null) return local

        return try {
            firestore.collection("usuarios").document(id).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

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
