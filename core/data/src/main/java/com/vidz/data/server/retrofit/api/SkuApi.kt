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


}

