package com.vidz.data.server.retrofit.dto

data class OrderDetailDto(
    val orderDetailId: Long = 0L,
    val orderId: Long = 0L,
    val sku: StockKeepingUnitDto = StockKeepingUnitDto(),
    val quantity: Int = 0,
    val promotionalCampaign: PromotionalCampaignDto? = null,
    val unitPrice: Double = 0.0,
    val subTotal: Double = 0.0,
    val finalTotal: Double = 0.0,
    val slot: SlotDto? = null,
    val createdAt: String = "",
    val updatedAt: String = ""
) 