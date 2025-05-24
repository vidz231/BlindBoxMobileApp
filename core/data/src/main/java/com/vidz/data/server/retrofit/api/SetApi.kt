package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.SetDto
import com.vidz.data.server.retrofit.dto.GetSets200Response
import retrofit2.Response
import retrofit2.http.*

interface SetApi {

    @POST("sets")
    suspend fun createSet(
        @Body setDto: SetDto
    ): Response<SetDto>

    @GET("sets/{setId}")
    suspend fun getSetById(
        @Path("setId") setId: Long
    ): Response<SetDto>

    @GET("sets")
    suspend fun getSets(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: List<String>? = null,
        @Query("filter") filter: String? = null,
        @Query("search") search: String? = null
    ): Response<GetSets200Response>

    @PUT("sets/{setId}")
    suspend fun updateSet(
        @Path("setId") setId: Long,
        @Body setDto: SetDto
    ): Response<SetDto>

    @DELETE("sets/{setId}")
    suspend fun deleteSet(
        @Path("setId") setId: Long
    ): Response<Unit>
} 