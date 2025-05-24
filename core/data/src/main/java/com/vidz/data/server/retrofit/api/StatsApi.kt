package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.StringBigDecimalDatapointDto
import com.vidz.data.server.retrofit.dto.StringIntegerDatapointDto
import com.vidz.data.server.retrofit.dto.TrendingProductDto
import retrofit2.Response
import retrofit2.http.*

interface StatsApi {

    @GET("stats/trending-products")
    suspend fun getTrendingProducts(): Response<List<TrendingProductDto>>

    @GET("stats/revenue-by-month")
    suspend fun getRevenueByMonth(): Response<List<StringBigDecimalDatapointDto>>

    @GET("stats/sales-by-category")
    suspend fun getSalesByCategory(): Response<List<StringIntegerDatapointDto>>

    @GET("stats/user-registrations-by-month")
    suspend fun getUserRegistrationsByMonth(): Response<List<StringIntegerDatapointDto>>

    @GET("stats/orders-by-status")
    suspend fun getOrdersByStatus(): Response<List<StringIntegerDatapointDto>>
} 