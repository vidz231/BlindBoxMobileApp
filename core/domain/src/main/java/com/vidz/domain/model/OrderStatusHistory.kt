package com.vidz.domain.model

data class OrderStatusHistory(
    val id: Long = 0L,
    val orderId: Long = 0L,
    val state: OrderStatus = OrderStatus.CREATED,
    val createdAt: String = ""
) 