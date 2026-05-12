package com.example.seguimiento.Data.repositorios

import android.content.Context
import android.net.Uri
import com.example.seguimiento.Dominio.repositorios.ImageStorageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

// --- API DEFINITION ---
data class ImgBBResponse(
    val data: ImgBBData?,
    val success: Boolean,
    val status: Int
)

data class ImgBBData(
    val url: String,
    val display_url: String,
    val delete_url: String
)

interface ImgBBApi {
    @Multipart
    @POST("upload")
    suspend fun uploadImage(
        @Query("key") apiKey: String,
        @Part image: MultipartBody.Part
    ): ImgBBResponse
}

@Singleton
class ImgBBRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageStorageRepository {

    // TODO: REEMPLAZA ESTO CON TU API KEY DE IMGBB (api.imgbb.com)
    private val API_KEY = "d4715557700e7cb470f662466a2383bf"

    private val api: ImgBBApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.imgbb.com/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImgBBApi::class.java)
    }

    override suspend fun uploadImage(uri: Uri, category: String, fileName: String): String? = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ImgBB", "Iniciando subida a ImgBB: $fileName")
            
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, bytes.size)
            val body = MultipartBody.Part.createFormData("image", fileName, requestFile)

            val response = api.uploadImage(API_KEY, body)

            if (response.success && response.data != null) {
                android.util.Log.d("ImgBB", "Subida exitosa: ${response.data.url}")
                response.data.url
            } else {
                android.util.Log.e("ImgBB", "Error en respuesta ImgBB: Status ${response.status}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("ImgBB", "Error crítico subiendo a ImgBB", e)
            null
        }
    }

    override suspend fun getImageUrl(fileId: String): String? {
        // En ImgBB, el "id" que guardamos es directamente la URL pública
        return fileId
    }
}
