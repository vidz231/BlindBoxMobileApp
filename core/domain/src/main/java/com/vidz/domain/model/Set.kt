package com.vidz.domain.model

data class SetDto(
    val setId: Long = 0L,
    val sku: StockKeepingUnit = StockKeepingUnit(),
    val isVisible: Boolean = true,
    val slots: List<Slot> = emptyList(),
    val blindBox: BlindBox = BlindBox(),
    val createdAt: String = "",
    val updatedAt: String = ""
) 