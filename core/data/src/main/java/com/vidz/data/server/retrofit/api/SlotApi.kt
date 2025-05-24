package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.SlotDto
import com.vidz.data.server.retrofit.dto.GetSlots200Response
import retrofit2.Response
import retrofit2.http.*

interface SlotApi {

    @POST("slots")
    suspend fun createSlot(
        @Body slotDto: SlotDto
    ): Response<SlotDto>

    @GET("slots/{slotId}")
    suspend fun getSlotById(
        @Path("slotId") slotId: Int
    ): Response<SlotDto>

    @GET("slots")
    suspend fun getSlots(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: List<String>? = null,
        @Query("filter") filter: String? = null,
        @Query("search") search: String? = null
    ): Response<GetSlots200Response>

    @PUT("slots/{slotId}")
    suspend fun updateSlot(
        @Path("slotId") slotId: Int,
        @Body slotDto: SlotDto
    ): Response<SlotDto>

    @DELETE("slots/{slotId}")
    suspend fun deleteSlot(
        @Path("slotId") slotId: Int
    ): Response<Unit>
} 