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

    @GET("brands/{brandId}")
    suspend fun getBrandById(
        @Path("brandId") brandId: Long
    ): Response<BrandDto>

    @POST("brands")
    suspend fun createBrand(
        @Body createRequest: CreateBrandRequest
    ): Response<BrandDto>

    @PUT("brands/{brandId}")
    suspend fun updateBrand(
        @Path("brandId") brandId: Long,
        @Body updateRequest: UpdateBrandRequest
    ): Response<BrandDto>

    @DELETE("brands/{brandId}")
    suspend fun deleteBrand(
        @Path("brandId") brandId: Long
    ): Response<Unit>
}

// Request DTOs
data class CreateBrandRequest(
    val name: String,
    val description: String,
    val isVisible: Boolean = true
)

data class UpdateBrandRequest(
    val name: String? = null,
    val description: String? = null,
    val isVisible: Boolean? = null
) 