package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.ShippingInfoDto
import com.vidz.data.server.retrofit.dto.GetShippingInfos200Response
import retrofit2.Response
import retrofit2.http.*

interface ShippingInfoApi {

    @POST("shipping-info")
    suspend fun createShippingInfo(
        @Body shippingInfoDto: ShippingInfoDto
    ): Response<ShippingInfoDto>

    @GET("shipping-info/{shippingInfoId}")
    suspend fun getShippingInfoById(
        @Path("shippingInfoId") shippingInfoId: Long
    ): Response<ShippingInfoDto>

    @GET("shipping-info")
    suspend fun getShippingInfos(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: List<String>? = null,
        @Query("filter") filter: String? = null,
        @Query("search") search: String? = null
    ): Response<GetShippingInfos200Response>

    @PUT("shipping-info/{shippingInfoId}")
    suspend fun updateShippingInfo(
        @Path("shippingInfoId") shippingInfoId: Long,
        @Body shippingInfoDto: ShippingInfoDto
    ): Response<ShippingInfoDto>

    @DELETE("shipping-info/{shippingInfoId}")
    suspend fun deleteShippingInfo(
        @Path("shippingInfoId") shippingInfoId: Long
    ): Response<Unit>
} 