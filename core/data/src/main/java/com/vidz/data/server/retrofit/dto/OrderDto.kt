package com.vidz.data.server.retrofit.dto

data class OrderDto(
    val orderId: Long = 0L,
    val account: AccountDto = AccountDto(),
    val orderStatusHistories: List<OrderStatusHistoryDto> = emptyList(),
    val latestStatus: OrderStatus = OrderStatus.CREATED,
    val orderDetails: List<OrderDetailDto> = emptyList(),
    val transaction: TransactionDto? = null,
    val voucher: VoucherDto? = null,
    val shippingInfo: ShippingInfoDto = ShippingInfoDto(),
    val createdAt: String = "",
    val updatedAt: String = "",
    val subTotal: Double = 0.0,
    val finalTotal: Double = 0.0
)

data class PlaceOrderSuccessResponse(
    val paymentRedirectUrl: String = "",
    val orderDto: OrderDto = OrderDto()
)

