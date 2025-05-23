package com.vidz.data.server.retrofit.dto


data class AccountDto(
    val accountId: Long = 0L,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val password: String? = null,
    val avatarUrl: String? = null,
    val balance: Double = 0.0,
    val updateBalanceAt: String? = null,
    val role: AccountRole = AccountRole.CUSTOMER,
    val isVerified: Boolean = false,
    val verifiedAt: String? = null,
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = "",
    val defaultShippingInfo: ShippingInfoDto? = null
)

enum class AccountRole {
    ADMIN, STAFF, CUSTOMER
}