package com.vidz.domain.model

data class ToyDto(
    val toyId: Long = 0L,
    val name: String = "",
    val description: String = "",
    val weight: Double = 0.0,
    val rarity: ToyRarity = ToyRarity.Regular,
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = "",
    val images: List<Image> = emptyList()
)

sealed class ToyRarity {
    data object Regular : ToyRarity()
    data object Secret : ToyRarity()
} 