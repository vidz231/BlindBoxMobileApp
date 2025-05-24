package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.ToyDto
import retrofit2.Response
import retrofit2.http.*

interface ToyApi {

    @GET("toys")
    suspend fun getToys(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("search") search: String? = null,
        @Query("filter") filter: String? = null
    ): Response<PagedResponse<ToyDto>>

    @GET("toys/{toyId}")
    suspend fun getToyById(
        @Path("toyId") toyId: Long
    ): Response<ToyDto>

}

// Request DTOs
data class CreateToyRequest(
    val name: String,
    val description: String,
    val weight: Double,
    val rarity: String, // REGULAR or SECRET
    val isVisible: Boolean = true
)

data class UpdateToyRequest(
    val name: String? = null,
    val description: String? = null,
    val weight: Double? = null,
    val rarity: String? = null,
    val isVisible: Boolean? = null
) 