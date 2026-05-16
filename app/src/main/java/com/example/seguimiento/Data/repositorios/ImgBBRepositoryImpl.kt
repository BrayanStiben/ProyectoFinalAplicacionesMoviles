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
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, bytes.size)
            val body = MultipartBody.Part.createFormData("image", fileName, requestFile)

            val response = api.uploadImage(API_KEY, body)

            if (response.success && response.data != null) {
                // IMPORTANTÍSIMO: Usamos 'url' que es el link directo al .jpg
                // y limpiamos los caracteres de escape \/ que pone la API de ImgBB
                val directUrl = response.data.url.replace("\\/", "/")
                android.util.Log.d("ImgBB", "Enlace directo generado: $directUrl")
                directUrl
            } else {
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("ImgBB", "Error crítico en subida", e)
            null
        }
    }

    override suspend fun getImageUrl(fileId: String): String? = fileId
}
