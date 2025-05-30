package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class StockKeepingUnit(
    val skuId: Long = 0L,
    val name: String = "",
    val image: Image = Image(),
    val price: Double = 0.0,
    val stock: Int = 0,
    val specCount: Int = 0,
    val blindBox: BlindBox = BlindBox(),
    val createdAt: String = "",
    val updatedAt: String = "",
    val isVisible: Boolean = true
) 