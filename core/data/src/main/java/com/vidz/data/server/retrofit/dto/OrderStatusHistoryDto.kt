package com.vidz.data.server.retrofit.dto

data class OrderStatusHistoryDto(
    val id: Long = 0L,
    val orderId: Long = 0L,
    val state: OrderStatus = OrderStatus.CREATED,
    val createdAt: String = ""
) 