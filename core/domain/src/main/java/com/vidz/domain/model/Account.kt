package com.vidz.domain.model


data class Account(
    val accountId: Long = 0L,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val password: String = "",
    val avatarUrl: String = "",
    val balance: Double = 0.0,
    val updateBalanceAt: String = "",
    val role: AccountRole = AccountRole.Customer,
    val isVerified: Boolean = false,
    val verifiedAt: String = "",
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = "",
    val defaultShippingInfo: ShippingInfo = ShippingInfo()
)

sealed class AccountRole {
    data object Admin : AccountRole()
    data object Staff : AccountRole()
    data object Customer : AccountRole()
}