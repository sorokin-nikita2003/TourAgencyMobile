package com.example.touragency.serverConnection

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5000/"

    val okHttpClient = OkHttpClient.Builder()
        .hostnameVerifier { _, _ -> true } // Игнорировать проверку хоста
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: AuthApi by lazy {
        instance.create(AuthApi::class.java)
    }
}