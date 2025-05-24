package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.OrderDto
import com.vidz.data.server.retrofit.dto.PlaceOrder200Response
import com.vidz.data.server.retrofit.dto.PlaceOrderSuccessResponse
import retrofit2.Response
import retrofit2.http.*

interface OrderApi {

    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("search") search: String? = null,
        @Query("filter") filter: String? = null
    ): Response<PagedResponse<OrderDto>>

    @GET("orders/{orderId}")
    suspend fun getOrderById(
        @Path("orderId") orderId: Long
    ): Response<OrderDto>

    @POST("orders?accountId={accountId}")
    suspend fun createOrder(
        @Body createRequest: CreateOrderRequest,
        @Path("accountId") accountId: Long
    ): Response<PlaceOrder200Response>



    @POST("orders/{orderId}/cancel")
    suspend fun cancelOrder(
        @Path("orderId") orderId: Long
    ): Response<OrderDto>



}

// Request DTOs
data class CreateOrderRequest(
    val accountId: Long,
    val shippingInfoId: Long,
    val items: List<OrderDetailRequest>,
    val voucherId: Long? = null
)

data class OrderDetailRequest(
    val skuId: Long,
    val quantity: Int,
    val slotId: Long? = null
)

