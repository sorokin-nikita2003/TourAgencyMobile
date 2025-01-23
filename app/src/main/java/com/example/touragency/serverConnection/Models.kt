package com.example.touragency.serverConnection

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

data class Order(
    val id: Int,
    val userId: String,
    val buyTime: String,
    val orderStatus: String,
    val paymentStatus: String,
    val price: Int,
    val user: User1?
)

data class User(
    val id: Int,
    val userName: String,
    val normalizedUserName: String,
    val email: String?,
    val normalizedEmail: String,
)

data class User1(
    val id: String,
    val userName: String,
    val normalizedUserName: String,
    val email: String?,
    val normalizedEmail: String,
)

data class OrderData(
    val UserId: String,
    val buyTime: String,
    val Price: Int,
)

data class Basket(
    val id: Int,
    val UserId: String,
    val PersonsCount: Int,
)

data class OrderId(
    val id: Int,
)

data class BankCard(
    val id: Int,
    val CardNum: String,
    val CVV: String,
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val passwordConfirm: String
)