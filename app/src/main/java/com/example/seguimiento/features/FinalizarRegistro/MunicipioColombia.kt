package com.example.seguimiento.features.FinalizarRegistro

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

// Modelo para ciudades
data class CityResponse(
    @SerializedName("name")
    val municipio: String? = null,
    @SerializedName("departmentId")
    val departmentId: Int? = null
)

// Modelo para departamentos
data class DepartmentResponse(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String? = null
)

interface ColombiaApiService {
    @GET("City")
    suspend fun getMunicipios(): List<CityResponse>

    @GET("Department")
    suspend fun getDepartamentos(): List<DepartmentResponse>
}