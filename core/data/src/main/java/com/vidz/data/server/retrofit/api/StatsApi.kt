package com.vidz.data.server.retrofit.api

import retrofit2.Response
import retrofit2.http.*

interface StatsApi {

    @GET("stats/revenue-by-sku")
    suspend fun getRevenueBysku(): Response<List<RevenueBySkuResponse>>

    @GET("stats/daily-order")
    suspend fun getDailyOrder(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<List<DailyOrderResponse>>

    @GET("stats/daily-revenue")
    suspend fun getDailyRevenue(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<List<DailyRevenueResponse>>

    @GET("stats/monthly-revenue")
    suspend fun getMonthlyRevenue(
        @Query("year") year: Int? = null
    ): Response<List<MonthlyRevenueResponse>>

    @GET("stats/daily-new-customers")
    suspend fun getDailyNewCustomers(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<List<DailyNewCustomersResponse>>

    @GET("stats/monthly-new-customers")
    suspend fun getMonthlyNewCustomers(
        @Query("year") year: Int? = null
    ): Response<List<MonthlyNewCustomersResponse>>

    @GET("stats/top-brands")
    suspend fun getTopSellingBrands(
        @Query("limit") limit: Int = 10
    ): Response<List<TopBrandResponse>>

    @GET("stats/revenue-by-blind-box")
    suspend fun getRevenueByBlindBox(): Response<List<RevenueByBlindBoxResponse>>

    @GET("stats/revenue-by-brand")
    suspend fun getRevenueByBrand(): Response<List<RevenueByBrandResponse>>

    @GET("stats/revenue-trend")
    suspend fun getRevenueTrend(
        @Query("period") period: String = "daily" // daily, weekly, monthly
    ): Response<List<RevenueTrendResponse>>

    @GET("stats/top-selling-skus")
    suspend fun getTopSellingSkus(
        @Query("limit") limit: Int = 10
    ): Response<List<TopSellingSkuResponse>>
}

// Response DTOs for Stats
data class RevenueBySkuResponse(
    val skuId: Long,
    val skuName: String,
    val totalRevenue: Double,
    val totalQuantitySold: Int
)

data class DailyOrderResponse(
    val date: String,
    val totalOrders: Int,
    val totalRevenue: Double
)

data class DailyRevenueResponse(
    val date: String,
    val totalRevenue: Double
)

data class MonthlyRevenueResponse(
    val month: Int,
    val year: Int,
    val totalRevenue: Double
)

data class DailyNewCustomersResponse(
    val date: String,
    val newCustomers: Int
)

data class MonthlyNewCustomersResponse(
    val month: Int,
    val year: Int,
    val newCustomers: Int
)

data class TopBrandResponse(
    val brandId: Long,
    val brandName: String,
    val totalRevenue: Double,
    val totalQuantitySold: Int
)

data class RevenueByBlindBoxResponse(
    val blindBoxId: Long,
    val blindBoxName: String,
    val totalRevenue: Double,
    val totalQuantitySold: Int
)

data class RevenueByBrandResponse(
    val brandId: Long,
    val brandName: String,
    val totalRevenue: Double,
    val totalQuantitySold: Int
)

data class RevenueTrendResponse(
    val period: String,
    val revenue: Double,
    val growth: Double
)

data class TopSellingSkuResponse(
    val skuId: Long,
    val skuName: String,
    val totalQuantitySold: Int,
    val totalRevenue: Double
) 