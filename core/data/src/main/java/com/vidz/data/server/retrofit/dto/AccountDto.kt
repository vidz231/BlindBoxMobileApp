package com.vidz.data.server.retrofit.dto

import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class AccountDto(
    val accountId: Long = 0L,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val avatarUrl: String = "",
    val role: RoleEnum = RoleEnum.CUSTOMER,
    val balance: Double = 0.0,
    val updateBalanceAt: String = "",
    val isVerified: Boolean = false,
    val verifiedAt: String = "",
    val isVisible: Boolean = true,
    val defaultShippingInfo: ShippingInfoDto? = null,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
enum class RoleEnum(val value: String) {
    ADMIN("ADMIN"),
    CUSTOMER("CUSTOMER")
}