package com.vidz.data.server.retrofit.dto


data class BlindBoxDto(
    val blindBoxId: Long = 0L,
    val brand: BrandDto = BrandDto(),
    val name: String = "",
    val description: String = "",
    val images: List<ImageDto> = emptyList(),
    val blindBoxCampaigns: List<BlindBoxCampaignDto> = emptyList(),
    val isVisible: Boolean = true,
    val toys: List<ToyDto> = emptyList(),
    val skus: List<StockKeepingUnitDto> = emptyList(),
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