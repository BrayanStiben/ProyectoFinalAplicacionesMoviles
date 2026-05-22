package com.example.seguimiento.Data.servicios

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class OpenRouterRequest(
    val model: String = "google/gemini-flash-1.5-8b",
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)

data class OpenRouterResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

interface OpenRouterApi {
    @POST("chat/completions")
    suspend fun complete(
        @Header("Authorization") authorization: String,
        @Header("HTTP-Referer") referer: String = "https://petadopta.com",
        @Header("X-Title") title: String = "PetAdopta",
        @Body request: OpenRouterRequest
    ): OpenRouterResponse
}
