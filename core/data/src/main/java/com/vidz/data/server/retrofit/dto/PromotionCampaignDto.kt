package com.vidz.data.server.retrofit.dto

import java.math.BigDecimal

data class PromotionCampaignDto(
    val promotionalCampaignId: Long = 0L,
    val title: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val discountRate: BigDecimal? = null,
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
) 