package com.vidz.data.server.retrofit.dto

data class SetDto(
    val setId: Long = 0L,
    val sku: StockKeepingUnitDto = StockKeepingUnitDto(),
    val isVisible: Boolean = true,
    val slots: List<SlotDto> = emptyList(),
    val blindBox: BlindBoxDto = BlindBoxDto(),
    val createdAt: String = "",
    val updatedAt: String = ""
) 