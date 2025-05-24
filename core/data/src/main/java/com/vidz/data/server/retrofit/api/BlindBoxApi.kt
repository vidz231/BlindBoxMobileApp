package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.BlindBoxDto
import retrofit2.Response
import retrofit2.http.*

interface BlindBoxApi {

    @GET("blind-boxes")
    suspend fun getBlindBoxes(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("search") search: String? = null,
        @Query("filter") filter: String? = null
    ): Response<PagedResponse<BlindBoxDto>>

    @GET("blind-boxes/{blindBoxId}")
    suspend fun getBlindBoxById(
        @Path("blindBoxId") blindBoxId: Long
    ): Response<BlindBoxDto>

}

// Request DTOs
data class CreateBlindBoxRequest(
    val brandId: Long,
    val name: String,
    val description: String,
    val isVisible: Boolean = true
)

data class UpdateBlindBoxRequest(
    val brandId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val isVisible: Boolean? = null
) 