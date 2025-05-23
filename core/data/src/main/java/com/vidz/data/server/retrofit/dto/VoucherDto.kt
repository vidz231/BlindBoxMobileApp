package com.vidz.data.server.retrofit.dto

data class VoucherDto(
    val voucherId: Long = 0L,
    val orderId: Long? = null,
    val account: AccountDto = AccountDto(),
    val code: String = "",
    val discountRate: Double = 0.0,
    val limitAmount: Double = 0.0,
    val state: VoucherState = VoucherState.AVAILABLE,
    val createdAt: String = "",
    val updatedAt: String = "",
    val expiredAt: String = ""
)

enum class VoucherState {
    USED, AVAILABLE, RESERVED, EXPIRED
} 