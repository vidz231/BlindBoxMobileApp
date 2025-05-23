package com.vidz.data.server.retrofit.dto

data class ToyDto(
    val toyId: Long = 0L,
    val name: String = "",
    val description: String = "",
    val weight: Double = 0.0,
    val rarity: ToyRarity = ToyRarity.REGULAR,
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = "",
    val images: List<ImageDto> = emptyList()
)

enum class ToyRarity {
    REGULAR, SECRET
} 