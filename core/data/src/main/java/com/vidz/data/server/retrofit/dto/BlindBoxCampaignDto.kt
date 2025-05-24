package com.vidz.data.server.retrofit.dto

data class BlindBoxCampaignDto(
    val blindBoxId: Long = 0L,
    val promotionalCampaignId: Long = 0L,
    val createdAt: String = "",
    val updatedAt: String = "",
    val isVisible: Boolean = true
) 