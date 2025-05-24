package com.vidz.data.server.retrofit.dto

import java.math.BigDecimal

data class PromotionalCampaignRequestDto(
    val campaignId: Long? = null,
    val title: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val discountRate: BigDecimal? = null,
    val isVisible: Boolean = true,
    val blindBoxIds: List<Long> = emptyList()
) 