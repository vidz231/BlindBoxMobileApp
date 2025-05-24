package com.vidz.data.server.retrofit.dto

data class CartDto(
    val voucherId: Long? = null,
    val items: List<CartItemDto> = emptyList(),
    val shippingInfoId: Long? = null,
    val paymentMethod: PaymentMethodEnum? = null
)

enum class PaymentMethodEnum(val value: String) {
    INTERNAL_WALLET("INTERNAL_WALLET"),
    PAYPAL("PAYPAL"),
    VNPAY("VNPAY")
} 