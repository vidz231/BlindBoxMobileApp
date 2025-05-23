package com.vidz.data.server.retrofit.dto


data class BrandDto(
    val brandId: Long = 0L,
    val name: String = "",
    val description: String = "",
    val isVisible: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
) 