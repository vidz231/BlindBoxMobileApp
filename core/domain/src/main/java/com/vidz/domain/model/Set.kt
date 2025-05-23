package com.vidz.domain.model

data class Set(
    val setId: Long = 0L,
    val sku: StockKeepingUnit = StockKeepingUnit(),
    val isVisible: Boolean = true,
    val slots: List<SlotDto> = emptyList(),
    val blindBox: BlindBoxDto = BlindBoxDto(),
    val createdAt: String = "",
    val updatedAt: String = ""
) 