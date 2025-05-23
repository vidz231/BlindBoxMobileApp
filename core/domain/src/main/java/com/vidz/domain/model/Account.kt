package com.vidz.domain.model


data class AccountDto(
    val accountId: Long = 0L,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val password: String? = null,
    val avatarUrl: String? = null,
    val balance: Double = 0.0,
    val updateBalanceAt: String? = null,
    val role: AccountRole = AccountRole.Customer,
    val isVerified: Boolean = false,
    val verifiedAt: String? = null,
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = "",
    val defaultShippingInfo: ShippingInfo? = null
)

sealed class AccountRole {
    data object Admin : AccountRole()
    data object Staff : AccountRole()
    data object Customer : AccountRole()
}