package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.CartDto
import com.vidz.data.server.retrofit.dto.PlaceOrder200Response
import retrofit2.Response
import retrofit2.http.*

interface SalesApi {

    @POST("sales/add-to-cart")
    suspend fun addToCart(
        @Body cartDto: CartDto
    ): Response<CartDto>

    @GET("sales/cart")
    suspend fun getCart(): Response<CartDto>

    @PUT("sales/cart")
    suspend fun updateCart(
        @Body cartDto: CartDto
    ): Response<CartDto>

    @DELETE("sales/cart")
    suspend fun clearCart(): Response<Unit>

    @POST("sales/place-order")
    suspend fun placeOrder(
        @Body cartDto: CartDto
    ): Response<PlaceOrder200Response>
} 