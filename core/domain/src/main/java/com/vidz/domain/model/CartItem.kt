package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val id: String = "", // Unique identifier for cart item (combination of skuId and slotId)
    val sku: StockKeepingUnit = StockKeepingUnit(),
    val quantity: Int = 1,
    val slot: Slot? = null,
    val addedAt: Long = System.currentTimeMillis()
) 