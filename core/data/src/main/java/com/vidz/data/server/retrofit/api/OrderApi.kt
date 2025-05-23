package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.OrderDto
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

    @POST("orders")
    suspend fun createOrder(
        @Body createRequest: CreateOrderRequest
    ): Response<OrderDto>

    @PUT("orders/{orderId}")
    suspend fun updateOrder(
        @Path("orderId") orderId: Long,
        @Body updateRequest: UpdateOrderRequest
    ): Response<OrderDto>

    @POST("orders/{orderId}/cancel")
    suspend fun cancelOrder(
        @Path("orderId") orderId: Long
    ): Response<OrderDto>

    @POST("orders/{orderId}/shipping")
    suspend fun markOrderAsShipping(
        @Path("orderId") orderId: Long
    ): Response<OrderDto>

    @POST("orders/{orderId}/ready")
    suspend fun markOrderAsReady(
        @Path("orderId") orderId: Long
    ): Response<OrderDto>

    @POST("orders/{orderId}/delivered")
    suspend fun markOrderAsDelivered(
        @Path("orderId") orderId: Long
    ): Response<OrderDto>

    @POST("orders/{orderId}/receive")
    suspend fun markOrderAsReceived(
        @Path("orderId") orderId: Long
    ): Response<OrderDto>
}

// Request DTOs
data class CreateOrderRequest(
    val accountId: Long,
    val shippingInfoId: Long,
    val orderDetails: List<OrderDetailRequest>,
    val voucherId: Long? = null
)

data class OrderDetailRequest(
    val skuId: Long,
    val quantity: Int,
    val promotionalCampaignId: Long? = null
)

data class UpdateOrderRequest(
    val shippingInfoId: Long? = null,
    val voucherId: Long? = null
) 