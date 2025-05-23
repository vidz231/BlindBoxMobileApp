package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.StockKeepingUnitDto
import retrofit2.Response
import retrofit2.http.*

interface SkuApi {

    @GET("skus")
    suspend fun getSkus(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("search") search: String? = null,
        @Query("filter") filter: String? = null
    ): Response<PagedResponse<StockKeepingUnitDto>>

    @GET("skus/{skuId}")
    suspend fun getSkuById(
        @Path("skuId") skuId: Long
    ): Response<StockKeepingUnitDto>

    @POST("skus")
    suspend fun createSku(
        @Body createRequest: CreateSkuRequest
    ): Response<StockKeepingUnitDto>

    @PUT("skus/{skuId}")
    suspend fun updateSku(
        @Path("skuId") skuId: Long,
        @Body updateRequest: UpdateSkuRequest
    ): Response<StockKeepingUnitDto>

    @DELETE("skus/{skuId}")
    suspend fun deleteSku(
        @Path("skuId") skuId: Long
    ): Response<Unit>
}

// Request DTOs
data class CreateSkuRequest(
    val name: String,
    val imageId: Long,
    val price: Double,
    val stock: Int,
    val specCount: Int,
    val blindBoxId: Long,
    val isVisible: Boolean = true
)

data class UpdateSkuRequest(
    val name: String? = null,
    val imageId: Long? = null,
    val price: Double? = null,
    val stock: Int? = null,
    val specCount: Int? = null,
    val blindBoxId: Long? = null,
    val isVisible: Boolean? = null
) 