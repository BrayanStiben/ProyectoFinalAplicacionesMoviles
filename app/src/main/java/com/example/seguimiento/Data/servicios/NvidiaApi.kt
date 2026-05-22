package com.example.seguimiento.Data.servicios

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class NvidiaMessage(
    val role: String,
    val content: String
)

data class NvidiaRequest(
    val model: String,
    val messages: List<NvidiaMessage>,
    val temperature: Double = 0.2,
    val top_p: Double = 0.7,
    val max_tokens: Int = 1024
)

data class NvidiaResponse(
    val choices: List<NvidiaChoice>
)

data class NvidiaChoice(
    val message: NvidiaMessage
)

interface NvidiaApi {
    @POST("chat/completions")
    suspend fun complete(
        @Header("Authorization") authorization: String,
        @Body request: NvidiaRequest
    ): NvidiaResponse
}
