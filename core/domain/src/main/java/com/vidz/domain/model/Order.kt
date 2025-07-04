package com.vidz.domain.model

data class OrderDto(
    val orderId: Long = 0L,
    val account: Account = Account(),
    val orderStatusHistories: List<OrderStatusHistory> = emptyList(),
    val latestStatus: OrderStatus = OrderStatus.Created,
    val orderDetails: List<OrderDetail> = emptyList(),
    val transaction: Transaction = Transaction(),
    val voucher: Voucher = Voucher(),
    val shippingInfo: ShippingInfo = ShippingInfo(),
    val createdAt: String = "",
    val updatedAt: String = "",
    val subTotal: Double = 0.0,
    val finalTotal: Double = 0.0
)

data class CreateOrderResult(
    val order: OrderDto,
    val paymentRedirectUrl: String
)

sealed class OrderStatus {
    data object Created : OrderStatus()
    data object Preparing : OrderStatus()
    data object PaymentFailed : OrderStatus()
    data object PaymentExpired : OrderStatus()
    data object Canceled : OrderStatus()
    data object ReadyForPickup : OrderStatus()
    data object Shipping : OrderStatus()
    data object Delivered : OrderStatus()
    data object Received : OrderStatus()
    data object Completed : OrderStatus()
} 