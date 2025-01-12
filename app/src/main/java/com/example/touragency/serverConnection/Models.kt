package com.example.touragency.serverConnection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.time.LocalDateTime

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val roles: List<String>
)

data class Tour(
    val id: Int,
    val price: Int,
    val tourName: String,
    val country: String,
    val description: String,
    val hotTour: Boolean,
    val dateStart: String,
    val deleted: Boolean,
//    val info: String
)
