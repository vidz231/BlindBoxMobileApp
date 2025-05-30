package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Toy(
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

@Serializable
sealed class ToyRarity {
    @Serializable
    data object Regular : ToyRarity()
    @Serializable
    data object Secret : ToyRarity()
} 