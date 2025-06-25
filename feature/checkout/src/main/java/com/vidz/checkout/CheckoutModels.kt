package com.vidz.checkout

enum class CheckoutType {
    FROM_CART,
    BUY_NOW
}

data class CheckoutItemData(
    val skuId: Long,
    val quantity: Int,
    val slotId: Long? = null,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val blindBoxName: String? = null,
    val slotNumber: Int? = null
) 