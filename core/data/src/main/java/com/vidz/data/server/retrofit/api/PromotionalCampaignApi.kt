package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.PromotionalCampaignDto
import com.vidz.data.server.retrofit.dto.PromotionalCampaignRequestDto
import com.vidz.data.server.retrofit.dto.GetPromotionalCampaigns200Response
import retrofit2.Response
import retrofit2.http.*

interface PromotionalCampaignApi {

    @POST("promotional-campaigns")
    suspend fun createPromotionalCampaign(
        @Body promotionalCampaignRequestDto: PromotionalCampaignRequestDto
    ): Response<PromotionalCampaignDto>

    @GET("promotional-campaigns/{campaignId}")
    suspend fun getPromotionalCampaignById(
        @Path("campaignId") campaignId: Long
    ): Response<PromotionalCampaignDto>

    @GET("promotional-campaigns")
    suspend fun getPromotionalCampaigns(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: List<String>? = null,
        @Query("filter") filter: String? = null,
        @Query("search") search: String? = null
    ): Response<GetPromotionalCampaigns200Response>

    @PUT("promotional-campaigns/{campaignId}")
    suspend fun updatePromotionalCampaign(
        @Path("campaignId") campaignId: Long,
        @Body promotionalCampaignRequestDto: PromotionalCampaignRequestDto
    ): Response<PromotionalCampaignDto>

    @DELETE("promotional-campaigns/{campaignId}")
    suspend fun deletePromotionalCampaign(
        @Path("campaignId") campaignId: Long
    ): Response<Unit>
} 