package com.example.touragency.serverConnection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("api/Auth/Login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("api/Tours/GetAll")
    fun getAllTours(@Header("Authorization") token: String): Call<List<Tour>>

    @POST("api/Tours/Create")
    fun addTour(
//        @Header("Authorization") token: String,
        @Body tour: Tour
    ): Call<Void>

}
