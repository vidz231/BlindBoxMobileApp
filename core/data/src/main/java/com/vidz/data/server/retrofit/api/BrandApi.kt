package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.BrandDto
import retrofit2.Response
import retrofit2.http.*

interface BrandApi {

    @GET("brands")
    suspend fun getBrands(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("search") search: String? = null,
        @Query("filter") filter: String? = null
    ): Response<PagedResponse<BrandDto>>

}
