package com.vidz.data.server.retrofit.dto

data class ShippingInfoDto(
    val shippingInfoId: Long = 0L,
    val name: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val ward: String = "",
    val district: String = "",
    val city: String = "",
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
) 