package com.vidz.domain.model

data class VoucherDto(
    val voucherId: Long = 0L,
    val orderId: Long? = null,
    val account: AccountDto = AccountDto(),
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