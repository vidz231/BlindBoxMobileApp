package com.vidz.domain.model


data class BlindBox(
    val blindBoxId: Long = 0L,
    val brand: Brand = Brand(),
    val name: String = "",
    val description: String = "",
    val images: List<Image> = emptyList(),
    val blindBoxCampaigns: List<BlindBoxCampaign> = emptyList(),
    val isVisible: Boolean = true,
    val toys: List<Toy> = emptyList(),
    val skus: List<StockKeepingUnit> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class BlindBoxCampaign(
    val blindBoxId: Long = 0L,
    val promotionalCampaignId: Long = 0L,
    val createdAt: String = "",
    val updatedAt: String = "",
    val isVisible: Boolean = true
)