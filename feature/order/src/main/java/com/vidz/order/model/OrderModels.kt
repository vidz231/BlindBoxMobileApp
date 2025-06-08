package com.vidz.order.model

enum class OrderStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    CANCELLED
}

data class OrderItem(
    val id: String,
    val orderNumber: String,
    val date: String,
    val status: OrderStatus,
    val totalAmount: Double,
    val items: List<OrderProductItem>,
    val shippingAddress: String? = null,
    val paymentMethod: String? = null,
    val note: String? = null,
    val estimatedDeliveryDate: String? = null
)

data class OrderProductItem(
    val name: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String? = null
) 