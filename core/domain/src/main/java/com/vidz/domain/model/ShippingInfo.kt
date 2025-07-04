package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ShippingInfo(
    val shippingInfoId: Long = 0L,
    val address: String = "",
    val ward: String = "",
    val district: String = "",
    val city: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
) 