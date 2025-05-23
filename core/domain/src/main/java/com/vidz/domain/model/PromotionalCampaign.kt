package com.vidz.domain.model

data class PromotionalCampaign(
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