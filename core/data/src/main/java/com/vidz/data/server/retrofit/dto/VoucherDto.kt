package com.vidz.data.server.retrofit.dto

import java.math.BigDecimal

data class VoucherDto(
    val voucherId: Long = 0L,
    val code: String = "",
    val name: String = "",
    val description: String = "",
    val discountType: DiscountTypeEnum = DiscountTypeEnum.PERCENTAGE,
    val discountValue: Float = 0.0f,
    val minimumOrderValue: Float =  0.0f,
    val maximumDiscount: Float = 0.0f,
    val usageLimit: Int = 0,
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