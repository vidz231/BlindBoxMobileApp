package com.vidz.data.server.retrofit.dto

data class StockKeepingUnitDto(
    val skuId: Long = 0L,
    val name: String = "",
    val image: ImageDto = ImageDto(),
    val price: Double = 0.0,
    val stock: Int = 0,
    val specCount: Int = 0,
    val blindBox: BlindBoxDto = BlindBoxDto(),
    val createdAt: String = "",
    val updatedAt: String = "",
    val isVisible: Boolean = true
) 