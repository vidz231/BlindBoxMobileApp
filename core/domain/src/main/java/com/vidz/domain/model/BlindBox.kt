package com.vidz.domain.model


data class BlindBoxDto(
    val blindBoxId: Long = 0L,
    val brand: Brand = Brand(),
    val name: String = "",
    val description: String = "",
    val images: List<Image> = emptyList(),
    val blindBoxCampaigns: List<BlindBoxCampaignDto> = emptyList(),
    val isVisible: Boolean = true,
    val toys: List<ToyDto> = emptyList(),
    val skus: List<StockKeepingUnit> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class BlindBoxCampaignDto(
    val blindBoxId: Long = 0L,
    val promotionalCampaignId: Long = 0L,
    val createdAt: String = "",
    val updatedAt: String = "",
    val isVisible: Boolean = true
) 