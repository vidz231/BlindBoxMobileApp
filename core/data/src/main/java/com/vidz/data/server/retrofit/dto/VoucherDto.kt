package com.vidz.data.server.retrofit.dto

import java.math.BigDecimal

data class VoucherDto(
    val voucherId: Long = 0L,
    val code: String = "",
    val name: String = "",
    val description: String = "",
    val discountType: DiscountTypeEnum = DiscountTypeEnum.PERCENTAGE,
    val discountValue: BigDecimal = BigDecimal.ZERO,
    val minimumOrderValue: BigDecimal? = null,
    val maximumDiscount: BigDecimal? = null,
    val usageLimit: Int? = null,
    val usedCount: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
)

enum class DiscountTypeEnum(val value: String) {
    PERCENTAGE("PERCENTAGE"),
    FIXED_AMOUNT("FIXED_AMOUNT")
} 