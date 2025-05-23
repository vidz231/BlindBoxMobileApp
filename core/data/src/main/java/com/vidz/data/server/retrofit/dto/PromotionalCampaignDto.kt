package com.vidz.data.server.retrofit.dto

data class PromotionalCampaignDto(
    val campaignId: Long = 0L,
    val title: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val discountRate: Double = 0.0,
    val isVisible: Boolean = true,
    val blindBoxCampaigns: List<BlindBoxCampaignDto> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
) 