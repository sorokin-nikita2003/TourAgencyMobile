package com.example.touragency.serverConnection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {
    @POST("api/Auth/Login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("api/Tours/GetAll")
    fun getAllTours(): Call<List<Tour>>

    @POST("api/Tours/Create")
    fun addTour(
//        @Header("Authorization") token: String,
        @Body tour: Tour
    ): Call<Void>

    @POST("api/Tours/Edit")
    fun editTour(@Body tour: Tour): Call<Void>

    @GET("api/Order/GetAll")
    fun getAllOrders(): Call<List<Order>>

    @POST("api/ShopCart/Add")
    fun createBasket(@Body basket: Basket): Call<Void>

    @POST("api/Order/Create")
    fun createOrder(@Body orderData: OrderData): Call<Void>

    @POST("api/Order/Submit")
    fun submitOrder(@Body orderId: OrderId): Call<Void>

    @POST("api/Order/Cancel")
    fun cancelOrder(@Body orderId: OrderId): Call<Void>

    @POST("api/Order/PaymentCard")
    fun Payment(@Body bankCard: BankCard): Call<Void>

    @POST("api/Tours/Delete/{id}")
    fun deleteTour(@Path("id") id: Int): Call<Void>
}

interface OnTourClickListener {
    fun onTourSubmit(tourName: String, pricePerPerson: Int, tourId: Int)
    fun onTourDelete(orderId: Int)
    fun onTourEdit(tour: Tour)
}
interface OnOrderClickListener {
    fun onOrderConfirm(orderId: Int)
    fun onOrderCancel(orderId: Int)
}