package com.vidz.domain.model

data class Voucher(
    val voucherId: Long = 0L,
    val orderId: Long = 0L,
    val account: Account = Account(),
    val code: String = "",
    val discountRate: Double = 0.0,
    val limitAmount: Double = 0.0,
    val state: VoucherState = VoucherState.Available,
    val createdAt: String = "",
    val updatedAt: String = "",
    val expiredAt: String = ""
)

sealed class VoucherState {
    data object Used : VoucherState()
    data object Available : VoucherState()
    data object Reserved : VoucherState()
    data object Expired : VoucherState()
}