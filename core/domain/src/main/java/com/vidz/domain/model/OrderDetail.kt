package com.vidz.domain.model

data class OrderDetail(
    val orderDetailId: Long = 0L,
    val orderId: Long = 0L,
    val sku: StockKeepingUnit = StockKeepingUnit(),
    val quantity: Int = 0,
    val promotionalCampaign: PromotionalCampaign? = null,
    val unitPrice: Double = 0.0,
    val subTotal: Double = 0.0,
    val finalTotal: Double = 0.0,
    val slot: Slot = Slot(),
    val createdAt: String = "",
    val updatedAt: String = ""
) 